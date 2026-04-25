package com.online.shop.service.payment;

import com.online.shop.payment.GatewayChargeRequest;
import com.online.shop.payment.GatewayChargeResult;
import com.online.shop.payment.GatewayRefundResult;
import com.online.shop.payment.IPaymentGateway;
import com.online.shop.core.strategy.PaymentStrategy;
import com.online.shop.model.Payment;
import com.online.shop.model.PaymentStatus;
import com.online.shop.repository.PaymentRepository;
import com.online.shop.service.payment.PaymentResult;
import com.online.shop.service.payment.IPaymentService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
public class PaymentServiceImpl implements IPaymentService {

  private final PaymentRepository paymentRepository;
  private final IPaymentGateway gateway;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public PaymentServiceImpl(PaymentRepository paymentRepository, IPaymentGateway gateway) {
    this.paymentRepository = paymentRepository;
    this.gateway = gateway;
  }

  @Override
  @Transactional
  public PaymentResult processPayment(
      PaymentStrategy strategy,
      String customerId,
      BigDecimal amount,
      String idempotencyKey,
      String currency,
      String description) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Payment amount must be positive");
    }

    Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
    if (existing.isPresent() && existing.get().getStatus() == PaymentStatus.COMPLETED) {
      Payment e = existing.get();
      return new PaymentResult(
          e, true, e.getTransactionId(), "Payment already completed (idempotent)");
    }

    GatewayChargeRequest probe =
        strategy.buildChargeRequest(amount, currency, idempotencyKey, description);
    Payment payment =
        new Payment(customerId, amount, currency, probe.getMethodType(), idempotencyKey);
    payment = paymentRepository.save(payment);

    GatewayChargeResult result = strategy.pay(gateway, amount, currency, idempotencyKey);

    if ("succeeded".equals(result.getStatus())) {
      return new PaymentResult(
          payment, true, result.getTransactionId(), result.getMessage());
    }

    payment.setStatus(PaymentStatus.FAILED);
    payment.setGatewayResponse(serialize(result.getRawResponse()));
    payment.setUpdatedAt(LocalDateTime.now());
    paymentRepository.save(payment);
    return new PaymentResult(payment, false, null, result.getMessage());
  }

  @Override
  @Transactional
  public Payment complete(
      String paymentId, String transactionId, String orderId, Map<String, Object> rawResponse) {
    Payment payment =
        paymentRepository
            .findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment " + paymentId + " not found"));
    payment.setStatus(PaymentStatus.COMPLETED);
    payment.setTransactionId(transactionId);
    payment.setOrderId(orderId);
    payment.setGatewayResponse(serialize(rawResponse));
    payment.setUpdatedAt(LocalDateTime.now());
    return paymentRepository.save(payment);
  }

  @Override
  @Transactional
  public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
    Payment payment =
        paymentRepository
            .findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment " + paymentId + " not found"));
    if (payment.getStatus() != PaymentStatus.COMPLETED) {
      throw new IllegalArgumentException("Only completed payments can be refunded");
    }

    GatewayRefundResult result = gateway.refund(payment.getTransactionId(), amount);
    if ("succeeded".equals(result.getStatus())) {
      payment.setStatus(PaymentStatus.REFUNDED);
      Map<String, Object> previous = parse(payment.getGatewayResponse());
      Map<String, Object> merged = new HashMap<>(previous);
      merged.put("refund_transaction_id", result.getRefundId());
      payment.setGatewayResponse(serialize(merged));
      payment.setUpdatedAt(LocalDateTime.now());
      paymentRepository.save(payment);
      return new PaymentResult(payment, true, result.getRefundId(), result.getMessage());
    }
    return new PaymentResult(payment, false, null, result.getMessage());
  }

  @Override
  public PaymentResult refundPayment(String paymentId) {
    return refundPayment(paymentId, null);
  }

  @Override
  public Optional<Payment> findById(String paymentId) {
    return paymentRepository.findById(paymentId);
  }

  @Override
  public List<Payment> findByCustomer(String customerId) {
    return paymentRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
  }

  private String serialize(Map<String, Object> raw) {
    if (raw == null) return "{}";
    try {
      return objectMapper.writeValueAsString(raw);
    } catch (Exception e) {
      return "{}";
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> parse(String json) {
    if (json == null || json.isEmpty()) return new HashMap<>();
    try {
      return objectMapper.readValue(json, Map.class);
    } catch (Exception e) {
      return new HashMap<>();
    }
  }
}

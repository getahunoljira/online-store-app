package com.online.shop.service;

import com.online.shop.domain.patterns.PaymentStrategy;
import com.online.shop.model.Payment;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IPaymentService {

  PaymentResult processPayment(
      PaymentStrategy strategy,
      String customerId,
      BigDecimal amount,
      String idempotencyKey,
      String currency,
      String description);

  Payment complete(
      String paymentId, String transactionId, String orderId, Map<String, Object> rawResponse);

  PaymentResult refundPayment(String paymentId, BigDecimal amount);

  PaymentResult refundPayment(String paymentId);

  Optional<Payment> findById(String paymentId);

  List<Payment> findByCustomer(String customerId);
}

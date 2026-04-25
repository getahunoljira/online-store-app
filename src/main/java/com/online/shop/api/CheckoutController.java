package com.online.shop.api;

import com.online.shop.model.dto.CheckoutRequest;
import com.online.shop.model.dto.CheckoutResponse;
import com.online.shop.model.dto.PaymentHistoryItem;
import com.online.shop.model.dto.PaymentSummary;
import com.online.shop.model.dto.RefundResponse;
import com.online.shop.exception.PaymentFailedException;
import com.online.shop.exception.PaymentNotFoundException;
import com.online.shop.core.strategy.CreditCardPaymentStrategy;
import com.online.shop.core.strategy.PaymentStrategy;
import com.online.shop.service.cart.ShoppingCart;
import com.online.shop.core.strategy.UpiPaymentStrategy;
import com.online.shop.model.Customer;
import com.online.shop.model.Order;
import com.online.shop.model.Payment;
import com.online.shop.model.PaymentStatus;
import com.online.shop.service.cart.ICartService;
import com.online.shop.service.customer.ICustomerService;
import com.online.shop.service.order.IOrderService;
import com.online.shop.service.payment.PaymentResult;
import com.online.shop.service.payment.IPaymentService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers/{customerId}/cart")
public class CheckoutController {

  private final ICustomerService customerService;
  private final ICartService cartService;
  private final IOrderService orderService;
  private final IPaymentService paymentService;

  public CheckoutController(
      ICustomerService customerService,
      ICartService cartService,
      IOrderService orderService,
      IPaymentService paymentService) {
    this.customerService = customerService;
    this.cartService = cartService;
    this.orderService = orderService;
    this.paymentService = paymentService;
  }

  @PostMapping("/checkout")
  public ResponseEntity<CheckoutResponse> checkout(
      @PathVariable String customerId, @Valid @RequestBody CheckoutRequest payload) {
    Customer customer = customerService.requireById(customerId);
    if (customer.getShippingAddress() == null) {
      throw new IllegalArgumentException(
          "Customer must have a shipping address before checkout");
    }

    ShoppingCart cart = cartService.getCart(customerId);
    if (cart.isEmpty()) {
      throw new IllegalArgumentException("Cart is empty");
    }

    BigDecimal total = cart.calculateTotal();
    PaymentStrategy strategy = buildStrategy(payload);
    String idempotencyKey = "checkout-" + customerId + "-" + UUID.randomUUID().toString().replace("-", "");

    PaymentResult paymentResult =
        paymentService.processPayment(
            strategy,
            customerId,
            total,
            idempotencyKey,
            payload.getCurrency(),
            "Order for customer " + customerId);

    if (!paymentResult.isSucceeded()) {
      throw new PaymentFailedException(
          paymentResult.getPayment().getId(), paymentResult.getMessage());
    }

    Order order;
    try {
      order = orderService.createOrder(customer, cart);
    } catch (IllegalArgumentException e) {
      paymentService.refundPayment(paymentResult.getPayment().getId(), total);
      throw e;
    }

    Map<String, Object> raw = new HashMap<>();
    raw.put("transaction_id", paymentResult.getTransactionId());
    raw.put("message", paymentResult.getMessage());
    paymentService.complete(
        paymentResult.getPayment().getId(), paymentResult.getTransactionId(), order.getId(), raw);

    cartService.clearCart(customerId);

    PaymentSummary summary =
        new PaymentSummary(
            paymentResult.getPayment().getId(),
            PaymentStatus.COMPLETED,
            paymentResult.getTransactionId(),
            total,
            payload.getCurrency(),
            payload.getPaymentMethod(),
            paymentResult.getMessage());

    return ResponseEntity.status(HttpStatus.CREATED).body(CheckoutResponse.from(order, summary));
  }

  @GetMapping("/payments")
  public List<PaymentHistoryItem> getPaymentHistory(@PathVariable String customerId) {
    customerService.requireById(customerId);
    return paymentService.findByCustomer(customerId).stream()
        .map(PaymentHistoryItem::from)
        .toList();
  }

  @PostMapping("/payments/{paymentId}/refund")
  public RefundResponse refund(
      @PathVariable String customerId, @PathVariable String paymentId) {
    customerService.requireById(customerId);
    Payment payment =
        paymentService
            .findById(paymentId)
            .filter(p -> customerId.equals(p.getCustomerId()))
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));

    PaymentResult result = paymentService.refundPayment(paymentId);

    if (!result.isSucceeded()) {
      throw new IllegalArgumentException(result.getMessage());
    }

    return new RefundResponse(
        paymentId, result.getPayment().getStatus(), result.getTransactionId(), result.getMessage());
  }

  private PaymentStrategy buildStrategy(CheckoutRequest payload) {
    if ("credit_card".equals(payload.getPaymentMethod())) {
      if (payload.getCardNumber() == null || payload.getCardNumber().isBlank()) {
        throw new IllegalArgumentException("card_number is required for credit_card payment");
      }
      return new CreditCardPaymentStrategy(
          payload.getCardNumber(),
          payload.getCardExpiryMonth(),
          payload.getCardExpiryYear(),
          payload.getCardCvv(),
          payload.getCardHolderName());
    }
    if (payload.getUpiId() == null || payload.getUpiId().isBlank()) {
      throw new IllegalArgumentException("upi_id is required for upi payment");
    }
    return new UpiPaymentStrategy(payload.getUpiId());
  }
}

package com.online.shop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.online.shop.payment.MockGatewayClient;
import com.online.shop.domain.patterns.CreditCardPaymentStrategy;
import com.online.shop.domain.patterns.UpiPaymentStrategy;
import com.online.shop.model.Payment;
import com.online.shop.model.PaymentStatus;
import com.online.shop.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PaymentServiceTest {

  private Payment makePayment(PaymentStatus status, String txnId) {
    Payment p = new Payment("cust-1", new BigDecimal("50.0"), "USD", "credit_card", "key-1");
    p.setStatus(status);
    p.setTransactionId(txnId);
    p.setGatewayResponse("{}");
    return p;
  }

  @Test
  void successfulCreditCardPayment() {
    PaymentRepository repo = mock(PaymentRepository.class);
    when(repo.findByIdempotencyKey("idem-1")).thenReturn(Optional.empty());
    when(repo.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

    IPaymentService svc = new com.online.shop.service.impl.PaymentServiceImpl(repo, new MockGatewayClient());
    CreditCardPaymentStrategy strategy = new CreditCardPaymentStrategy("4111111111111111");
    PaymentResult result =
        svc.processPayment(strategy, "cust-1", new BigDecimal("50.0"), "idem-1", "USD", "");

    assertThat(result.isSucceeded()).isTrue();
    assertThat(result.getTransactionId()).startsWith("mock_txn_");
    verify(repo, times(1)).save(any(Payment.class));
  }

  @Test
  void successfulUpiPayment() {
    PaymentRepository repo = mock(PaymentRepository.class);
    when(repo.findByIdempotencyKey("idem-2")).thenReturn(Optional.empty());
    when(repo.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

    IPaymentService svc = new com.online.shop.service.impl.PaymentServiceImpl(repo, new MockGatewayClient());
    UpiPaymentStrategy strategy = new UpiPaymentStrategy("user@bank");
    PaymentResult result =
        svc.processPayment(strategy, "cust-1", new BigDecimal("30.0"), "idem-2", "USD", "");

    assertThat(result.isSucceeded()).isTrue();
    verify(repo, times(1)).save(any(Payment.class));
  }

  @Test
  void declinedCardReturnsFailure() {
    PaymentRepository repo = mock(PaymentRepository.class);
    when(repo.findByIdempotencyKey("idem-3")).thenReturn(Optional.empty());
    when(repo.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

    IPaymentService svc = new com.online.shop.service.impl.PaymentServiceImpl(repo, new MockGatewayClient());
    CreditCardPaymentStrategy strategy = new CreditCardPaymentStrategy("4000000000000002");
    PaymentResult result =
        svc.processPayment(strategy, "cust-1", new BigDecimal("50.0"), "idem-3", "USD", "");

    assertThat(result.isSucceeded()).isFalse();
    assertThat(result.getTransactionId()).isNull();
    assertThat(result.getPayment().getStatus()).isEqualTo(PaymentStatus.FAILED);
    verify(repo, times(2)).save(any(Payment.class));
  }

  @Test
  void invalidAmountRaises() {
    IPaymentService svc = new com.online.shop.service.impl.PaymentServiceImpl(mock(PaymentRepository.class), new MockGatewayClient());
    CreditCardPaymentStrategy strategy = new CreditCardPaymentStrategy("4111111111111111");
    assertThatThrownBy(
            () ->
                svc.processPayment(
                    strategy, "cust-1", BigDecimal.ZERO, "idem-4", "USD", ""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("positive");
  }

  @Test
  void idempotencyReturnsExistingCompleted() {
    PaymentRepository repo = mock(PaymentRepository.class);
    Payment existing = makePayment(PaymentStatus.COMPLETED, "txn_existing");
    when(repo.findByIdempotencyKey("idem-dup")).thenReturn(Optional.of(existing));

    IPaymentService svc = new com.online.shop.service.impl.PaymentServiceImpl(repo, new MockGatewayClient());
    CreditCardPaymentStrategy strategy = new CreditCardPaymentStrategy("4111111111111111");
    PaymentResult result =
        svc.processPayment(strategy, "cust-1", new BigDecimal("50.0"), "idem-dup", "USD", "");

    assertThat(result.isSucceeded()).isTrue();
    assertThat(result.getTransactionId()).isEqualTo("txn_existing");
    verify(repo, never()).save(any(Payment.class));
  }

  @Test
  void refundCompletedPayment() {
    PaymentRepository repo = mock(PaymentRepository.class);
    Payment completed = makePayment(PaymentStatus.COMPLETED, "txn_abc");
    when(repo.findById(completed.getId())).thenReturn(Optional.of(completed));
    when(repo.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

    IPaymentService svc = new com.online.shop.service.impl.PaymentServiceImpl(repo, new MockGatewayClient());
    PaymentResult result = svc.refundPayment(completed.getId());

    assertThat(result.isSucceeded()).isTrue();
    assertThat(result.getPayment().getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    verify(repo, times(1)).save(any(Payment.class));
  }

  @Test
  void refundNonCompletedRaises() {
    PaymentRepository repo = mock(PaymentRepository.class);
    Payment failed = makePayment(PaymentStatus.FAILED, null);
    when(repo.findById(failed.getId())).thenReturn(Optional.of(failed));

    IPaymentService svc = new com.online.shop.service.impl.PaymentServiceImpl(repo, new MockGatewayClient());
    assertThatThrownBy(() -> svc.refundPayment(failed.getId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Only completed");
  }

  @Test
  void refundMissingPaymentRaises() {
    PaymentRepository repo = mock(PaymentRepository.class);
    when(repo.findById("no-such-id")).thenReturn(Optional.empty());

    IPaymentService svc = new com.online.shop.service.impl.PaymentServiceImpl(repo, new MockGatewayClient());
    assertThatThrownBy(() -> svc.refundPayment("no-such-id"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("not found");
  }
}

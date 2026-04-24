package com.online.shop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.online.shop.model.Payment;
import com.online.shop.model.PaymentStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class PaymentRepositoryTest {

  @Autowired private PaymentRepository paymentRepository;

  private Payment makePayment(String customerId, String key, PaymentStatus status) {
    Payment p =
        new Payment(customerId, new BigDecimal("99.99"), "USD", "credit_card", key);
    p.setStatus(status);
    return p;
  }

  @Test
  void findByIdempotencyKeyReturnsExisting() {
    Payment p = paymentRepository.save(makePayment("cust-1", "key-1", PaymentStatus.PENDING));
    Optional<Payment> found = paymentRepository.findByIdempotencyKey("key-1");
    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(p.getId());
  }

  @Test
  void findByIdempotencyKeyReturnsEmptyWhenMissing() {
    assertThat(paymentRepository.findByIdempotencyKey("no-such-key")).isEmpty();
  }

  @Test
  void findByOrderId() {
    Payment p = makePayment("cust-1", "key-2", PaymentStatus.COMPLETED);
    p.setOrderId("order-42");
    paymentRepository.save(p);
    Optional<Payment> found = paymentRepository.findByOrderId("order-42");
    assertThat(found).isPresent();
    assertThat(found.get().getCustomerId()).isEqualTo("cust-1");
  }

  @Test
  void findByCustomerOrdersByCreatedAtDesc() {
    paymentRepository.save(makePayment("cust-a", "k1", PaymentStatus.COMPLETED));
    paymentRepository.save(makePayment("cust-a", "k2", PaymentStatus.FAILED));
    paymentRepository.save(makePayment("cust-b", "k3", PaymentStatus.COMPLETED));

    List<Payment> forA = paymentRepository.findByCustomerIdOrderByCreatedAtDesc("cust-a");
    assertThat(forA).hasSize(2);
    assertThat(forA).allMatch(p -> p.getCustomerId().equals("cust-a"));
  }
}

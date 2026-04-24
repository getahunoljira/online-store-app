package com.online.shop.repository;

import com.online.shop.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {

  Optional<Payment> findByIdempotencyKey(String idempotencyKey);

  Optional<Payment> findByOrderId(String orderId);

  List<Payment> findByCustomerIdOrderByCreatedAtDesc(String customerId);
}

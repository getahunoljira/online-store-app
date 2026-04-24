package com.online.shop.repository;

import com.online.shop.model.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

  Optional<Cart> findByCustomerId(String customerId);
}

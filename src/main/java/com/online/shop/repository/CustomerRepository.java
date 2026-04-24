package com.online.shop.repository;

import com.online.shop.model.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {

  Optional<Customer> findByEmail(String email);

  Optional<Customer> findByAccountUsername(String username);
}

package com.online.shop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.online.shop.model.Account;
import com.online.shop.model.Address;
import com.online.shop.model.Customer;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class CustomerRepositoryTest {

  @Autowired private CustomerRepository repository;

  private Customer makeCustomer(String email) {
    String username = "user_" + email.split("@")[0];
    Account account = new Account(username, "hashed_pass");
    Address address = new Address("1 Main St", "City", "ST", "00000");
    return new Customer("Test User", email, account, address);
  }

  private Customer makeCustomer() {
    return makeCustomer("test@example.com");
  }

  @Test
  void createAndFindById() {
    Customer saved = repository.save(makeCustomer());
    Customer fetched = repository.findById(saved.getId()).orElseThrow();
    assertThat(fetched.getEmail()).isEqualTo("test@example.com");
    assertThat(fetched.getName()).isEqualTo("Test User");
  }

  @Test
  void findByEmail() {
    repository.save(makeCustomer("alice@example.com"));
    Optional<Customer> fetched = repository.findByEmail("alice@example.com");
    assertThat(fetched).isPresent();
    assertThat(fetched.get().getName()).isEqualTo("Test User");
  }

  @Test
  void findByEmailNotFound() {
    assertThat(repository.findByEmail("nobody@example.com")).isEmpty();
  }

  @Test
  void findAll() {
    repository.save(makeCustomer("a@example.com"));
    repository.save(makeCustomer("b@example.com"));
    assertThat(repository.findAll()).hasSize(2);
  }

  @Test
  void updateShippingAddress() {
    Customer saved = repository.save(makeCustomer());
    Address existing = saved.getShippingAddress();
    existing.setStreet("999 Oak Ave");
    existing.setCity("Newtown");
    existing.setState("CA");
    existing.setZipCode("99999");
    repository.save(saved);

    Customer updated = repository.findById(saved.getId()).orElseThrow();
    assertThat(updated.getShippingAddress().getCity()).isEqualTo("Newtown");
  }

  @Test
  void deleteById() {
    Customer saved = repository.save(makeCustomer());
    repository.deleteById(saved.getId());
    assertThat(repository.findById(saved.getId())).isEmpty();
  }

  @Test
  void findByAccountUsername() {
    repository.save(makeCustomer("alice@example.com"));
    Optional<Customer> fetched = repository.findByAccountUsername("user_alice");
    assertThat(fetched).isPresent();
    assertThat(fetched.get().getEmail()).isEqualTo("alice@example.com");
  }
}

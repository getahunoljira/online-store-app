package com.online.shop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.online.shop.model.Order;
import com.online.shop.model.OrderLineItem;
import com.online.shop.model.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class OrderRepositoryTest {

  @Autowired private OrderRepository repository;

  private Order makeOrder(String customerId) {
    OrderLineItem li = new OrderLineItem("prod-1", "Widget", 2, new BigDecimal("15.0"));
    return new Order(
        customerId, List.of(li), "1 St", "City", "ST", "00000", new BigDecimal("30.0"));
  }

  @Test
  void createAndGetOrder() {
    Order saved = repository.save(makeOrder("cust-1"));
    Order fetched = repository.findById(saved.getId()).orElseThrow();
    assertThat(fetched.getTotalAmount()).isEqualByComparingTo(new BigDecimal("30.0"));
    assertThat(fetched.getItems()).hasSize(1);
    assertThat(fetched.getItems().get(0).getProductName()).isEqualTo("Widget");
  }

  @Test
  void findByCustomerId() {
    repository.save(makeOrder("cust-1"));
    repository.save(makeOrder("cust-1"));
    repository.save(makeOrder("cust-2"));
    List<Order> orders = repository.findByCustomerId("cust-1");
    assertThat(orders).hasSize(2);
  }

  @Test
  void updateStatus() {
    Order saved = repository.save(makeOrder("cust-1"));
    saved.setStatus(OrderStatus.SHIPPED);
    repository.save(saved);
    Order fetched = repository.findById(saved.getId()).orElseThrow();
    assertThat(fetched.getStatus()).isEqualTo(OrderStatus.SHIPPED);
  }

  @Test
  void deleteOrder() {
    Order saved = repository.save(makeOrder("cust-1"));
    repository.deleteById(saved.getId());
    assertThat(repository.findById(saved.getId())).isEmpty();
  }
}

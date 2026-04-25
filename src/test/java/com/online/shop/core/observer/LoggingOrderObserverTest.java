package com.online.shop.core.observer;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.online.shop.core.observer.LoggingOrderObserver;
import com.online.shop.model.Order;
import com.online.shop.model.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoggingOrderObserverTest {

  @Test
  void updateDoesNotThrow() {
    Order order =
        new Order(
            "order-1",
            "cust-1",
            List.of(),
            "1 St",
            "City",
            "ST",
            "00000",
            new BigDecimal("50.0"));
    order.setStatus(OrderStatus.SHIPPED);

    assertThatCode(() -> new LoggingOrderObserver().update(order)).doesNotThrowAnyException();
  }
}

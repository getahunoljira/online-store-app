package com.online.shop.core.observer;

import static org.assertj.core.api.Assertions.assertThat;

import com.online.shop.core.observer.OrderObserver;
import com.online.shop.core.state.PlacedState;
import com.online.shop.model.Order;
import com.online.shop.model.OrderLineItem;
import com.online.shop.model.OrderStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderObserverTest {

  private static class RecordingObserver implements OrderObserver {
    final List<OrderStatus> calls = new ArrayList<>();

    @Override
    public void update(Order order) {
      calls.add(order.getStatus());
    }
  }

  private Order makeOrder() {
    OrderLineItem li = new OrderLineItem("p1", "Gadget", 1, new BigDecimal("20.0"));
    Order order =
        new Order(
            "cust-1", List.of(li), "1 St", "City", "ST", "00000", new BigDecimal("20.0"));
    order.setState(new PlacedState());
    return order;
  }

  @Test
  void observerNotifiedOnShip() {
    Order order = makeOrder();
    RecordingObserver observer = new RecordingObserver();
    order.addObserver(observer);
    order.shipOrder();
    assertThat(observer.calls).contains(OrderStatus.SHIPPED);
  }

  @Test
  void observerNotifiedOnCancel() {
    Order order = makeOrder();
    RecordingObserver observer = new RecordingObserver();
    order.addObserver(observer);
    order.cancelOrder();
    assertThat(observer.calls).contains(OrderStatus.CANCELLED);
  }

  @Test
  void observerRemoved() {
    Order order = makeOrder();
    RecordingObserver observer = new RecordingObserver();
    order.addObserver(observer);
    order.removeObserver(observer);
    order.cancelOrder();
    assertThat(observer.calls).isEmpty();
  }
}

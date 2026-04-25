package com.online.shop.core.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.online.shop.core.state.PlacedState;
import com.online.shop.model.Order;
import com.online.shop.model.OrderLineItem;
import com.online.shop.model.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderStateTest {

  private Order makeOrder() {
    OrderLineItem li = new OrderLineItem("p1", "Widget", 1, new BigDecimal("10.0"));
    Order order =
        new Order(
            "cust-1", List.of(li), "1 St", "City", "ST", "00000", new BigDecimal("10.0"));
    order.setState(new PlacedState());
    return order;
  }

  @Test
  void placedToShipped() {
    Order order = makeOrder();
    order.shipOrder();
    assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
  }

  @Test
  void shippedToDelivered() {
    Order order = makeOrder();
    order.shipOrder();
    order.deliverOrder();
    assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
  }

  @Test
  void placedToCancelled() {
    Order order = makeOrder();
    order.cancelOrder();
    assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
  }

  @Test
  void cannotDeliverPlaced() {
    Order order = makeOrder();
    assertThatThrownBy(order::deliverOrder).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void cannotCancelShipped() {
    Order order = makeOrder();
    order.shipOrder();
    assertThatThrownBy(order::cancelOrder).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void cannotCancelDelivered() {
    Order order = makeOrder();
    order.shipOrder();
    order.deliverOrder();
    assertThatThrownBy(order::cancelOrder).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void cannotShipDelivered() {
    Order order = makeOrder();
    order.shipOrder();
    order.deliverOrder();
    assertThatThrownBy(order::shipOrder).isInstanceOf(IllegalStateException.class);
  }
}

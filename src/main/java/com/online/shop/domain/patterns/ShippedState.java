package com.online.shop.domain.patterns;

import com.online.shop.model.Order;
import com.online.shop.model.OrderStatus;

public class ShippedState implements OrderState {

  @Override
  public void shipOrder(Order order) {
    throw new IllegalStateException("Order is already shipped");
  }

  @Override
  public void deliverOrder(Order order) {
    order.setStatus(OrderStatus.DELIVERED);
    order.setState(new DeliveredState());
    order.notifyObservers();
  }

  @Override
  public void cancelOrder(Order order) {
    throw new IllegalStateException("Cannot cancel a shipped order");
  }
}

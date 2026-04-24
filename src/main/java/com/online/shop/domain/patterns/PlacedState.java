package com.online.shop.domain.patterns;

import com.online.shop.model.Order;
import com.online.shop.model.OrderStatus;

public class PlacedState implements OrderState {

  @Override
  public void shipOrder(Order order) {
    order.setStatus(OrderStatus.SHIPPED);
    order.setState(new ShippedState());
    order.notifyObservers();
  }

  @Override
  public void deliverOrder(Order order) {
    throw new IllegalStateException("Cannot deliver an order that hasn't been shipped yet");
  }

  @Override
  public void cancelOrder(Order order) {
    order.setStatus(OrderStatus.CANCELLED);
    order.setState(new CancelledState());
    order.notifyObservers();
  }
}

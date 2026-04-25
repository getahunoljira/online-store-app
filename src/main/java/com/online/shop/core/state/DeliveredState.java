package com.online.shop.core.state;

import com.online.shop.model.Order;

public class DeliveredState implements OrderState {

  @Override
  public void shipOrder(Order order) {
    throw new IllegalStateException("Order has already been delivered");
  }

  @Override
  public void deliverOrder(Order order) {
    throw new IllegalStateException("Order has already been delivered");
  }

  @Override
  public void cancelOrder(Order order) {
    throw new IllegalStateException("Cannot cancel a delivered order");
  }
}

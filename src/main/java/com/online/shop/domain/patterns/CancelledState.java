package com.online.shop.domain.patterns;

import com.online.shop.model.Order;

public class CancelledState implements OrderState {

  @Override
  public void shipOrder(Order order) {
    throw new IllegalStateException("Cannot ship a cancelled order");
  }

  @Override
  public void deliverOrder(Order order) {
    throw new IllegalStateException("Cannot deliver a cancelled order");
  }

  @Override
  public void cancelOrder(Order order) {
    throw new IllegalStateException("Order is already cancelled");
  }
}

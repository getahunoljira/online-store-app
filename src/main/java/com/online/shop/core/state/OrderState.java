package com.online.shop.core.state;

import com.online.shop.model.Order;

public interface OrderState {
  void shipOrder(Order order);

  void deliverOrder(Order order);

  void cancelOrder(Order order);
}

package com.online.shop.domain.patterns;

import com.online.shop.model.Order;

public interface OrderState {
  void shipOrder(Order order);

  void deliverOrder(Order order);

  void cancelOrder(Order order);
}

package com.online.shop.core.observer;

import com.online.shop.model.Order;

public interface OrderObserver {
  void update(Order order);
}

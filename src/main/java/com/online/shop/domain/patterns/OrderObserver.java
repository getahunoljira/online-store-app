package com.online.shop.domain.patterns;

import com.online.shop.model.Order;

public interface OrderObserver {
  void update(Order order);
}

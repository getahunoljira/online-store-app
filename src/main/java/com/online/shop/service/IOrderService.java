package com.online.shop.service;

import com.online.shop.domain.patterns.OrderObserver;
import com.online.shop.domain.patterns.ShoppingCart;
import com.online.shop.model.Customer;
import com.online.shop.model.Order;
import java.util.List;

public interface IOrderService {

  void addObserver(OrderObserver observer);

  Order createOrder(Customer customer, ShoppingCart cart);

  Order shipOrder(String orderId);

  Order deliverOrder(String orderId);

  Order cancelOrder(String orderId);

  Order getOrder(String orderId);

  List<Order> getCustomerOrders(String customerId);

  List<Order> getAllOrders();
}

package com.online.shop.service.order;

import com.online.shop.core.state.CancelledState;
import com.online.shop.core.state.DeliveredState;
import com.online.shop.core.observer.OrderObserver;
import com.online.shop.core.state.OrderState;
import com.online.shop.core.state.PlacedState;
import com.online.shop.core.state.ShippedState;
import com.online.shop.service.cart.ShoppingCart;
import com.online.shop.service.cart.ShoppingCartItem;
import com.online.shop.exception.OrderNotFoundException;
import com.online.shop.model.Customer;
import com.online.shop.model.Order;
import com.online.shop.model.OrderLineItem;
import com.online.shop.model.OrderStatus;
import com.online.shop.repository.OrderRepository;
import com.online.shop.service.inventory.IInventoryService;
import com.online.shop.service.order.IOrderService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements IOrderService {

  private final OrderRepository orderRepository;
  private final IInventoryService inventoryService;
  private final List<OrderObserver> observers = new ArrayList<>();

  public OrderServiceImpl(
      OrderRepository orderRepository,
      IInventoryService inventoryService,
      List<OrderObserver> observers) {
    this.orderRepository = orderRepository;
    this.inventoryService = inventoryService;
    this.observers.addAll(observers);
  }

  @Override
  public void addObserver(OrderObserver observer) {
    observers.add(observer);
  }

  @Override
  @Transactional
  public Order createOrder(Customer customer, ShoppingCart cart) {
    if (cart.isEmpty()) {
      throw new IllegalArgumentException("Cannot create an order from an empty cart");
    }
    if (customer.getShippingAddress() == null) {
      throw new IllegalArgumentException(
          "Customer must have a shipping address to place an order");
    }

    List<OrderLineItem> lineItems = new ArrayList<>();
    for (ShoppingCartItem item : cart.getItems().values()) {
      lineItems.add(
          new OrderLineItem(
              item.getProduct().getId(),
              item.getProduct().getName(),
              item.getQuantity(),
              item.getProduct().getPrice()));
    }

    inventoryService.updateStockForOrderList(lineItems);

    Order order =
        new Order(
            customer.getId(),
            lineItems,
            customer.getShippingAddress().getStreet(),
            customer.getShippingAddress().getCity(),
            customer.getShippingAddress().getState(),
            customer.getShippingAddress().getZipCode(),
            cart.calculateTotal());
    order.setState(new PlacedState());
    for (OrderObserver observer : observers) {
      order.addObserver(observer);
    }

    Order saved = orderRepository.save(order);
    saved.setState(new PlacedState());
    for (OrderObserver observer : observers) {
      saved.addObserver(observer);
    }
    return saved;
  }

  @Override
  @Transactional
  public Order shipOrder(String orderId) {
    Order order = getOrderOrRaise(orderId);
    order.shipOrder();
    orderRepository.save(order);
    return order;
  }

  @Override
  @Transactional
  public Order deliverOrder(String orderId) {
    Order order = getOrderOrRaise(orderId);
    order.deliverOrder();
    orderRepository.save(order);
    return order;
  }

  @Override
  @Transactional
  public Order cancelOrder(String orderId) {
    Order order = getOrderOrRaise(orderId);
    order.cancelOrder();
    orderRepository.save(order);
    return order;
  }

  @Override
  public Order getOrder(String orderId) {
    return orderRepository
        .findById(orderId)
        .map(this::attachState)
        .orElse(null);
  }

  @Override
  public List<Order> getCustomerOrders(String customerId) {
    return orderRepository.findByCustomerId(customerId).stream().map(this::attachState).toList();
  }

  @Override
  public List<Order> getAllOrders() {
    return orderRepository.findAll().stream().map(this::attachState).toList();
  }

  private Order getOrderOrRaise(String orderId) {
    return orderRepository
        .findById(orderId)
        .map(this::attachState)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
  }

  private Order attachState(Order order) {
    order.setState(stateFor(order.getStatus()));
    for (OrderObserver observer : observers) {
      order.addObserver(observer);
    }
    return order;
  }

  private OrderState stateFor(OrderStatus status) {
    return switch (status) {
      case PLACED, PENDING_PAYMENT -> new PlacedState();
      case SHIPPED -> new ShippedState();
      case DELIVERED -> new DeliveredState();
      case CANCELLED -> new CancelledState();
      case RETURNED -> new CancelledState();
    };
  }
}

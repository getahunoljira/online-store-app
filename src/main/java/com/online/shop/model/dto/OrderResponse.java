package com.online.shop.model.dto;

import com.online.shop.model.Order;
import com.online.shop.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

  private String id;
  private String customerId;
  private OrderStatus status;
  private BigDecimal totalAmount;
  private LocalDateTime orderDate;
  private List<OrderLineItemResponse> items;

  public OrderResponse() {}

  public OrderResponse(
      String id,
      String customerId,
      OrderStatus status,
      BigDecimal totalAmount,
      LocalDateTime orderDate,
      List<OrderLineItemResponse> items) {
    this.id = id;
    this.customerId = customerId;
    this.status = status;
    this.totalAmount = totalAmount;
    this.orderDate = orderDate;
    this.items = items;
  }

  public static OrderResponse from(Order order) {
    return new OrderResponse(
        order.getId(),
        order.getCustomerId(),
        order.getStatus(),
        order.getTotalAmount(),
        order.getOrderDate(),
        order.getItems().stream().map(OrderLineItemResponse::from).toList());
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public List<OrderLineItemResponse> getItems() {
    return items;
  }

  public void setItems(List<OrderLineItemResponse> items) {
    this.items = items;
  }
}

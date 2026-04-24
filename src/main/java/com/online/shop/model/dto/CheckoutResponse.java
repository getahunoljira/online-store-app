package com.online.shop.model.dto;

import com.online.shop.model.Order;
import com.online.shop.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CheckoutResponse {

  private String orderId;
  private OrderStatus orderStatus;
  private LocalDateTime orderDate;
  private BigDecimal totalAmount;
  private List<OrderLineItemResponse> items;
  private PaymentSummary payment;

  public CheckoutResponse() {}

  public CheckoutResponse(
      String orderId,
      OrderStatus orderStatus,
      LocalDateTime orderDate,
      BigDecimal totalAmount,
      List<OrderLineItemResponse> items,
      PaymentSummary payment) {
    this.orderId = orderId;
    this.orderStatus = orderStatus;
    this.orderDate = orderDate;
    this.totalAmount = totalAmount;
    this.items = items;
    this.payment = payment;
  }

  public static CheckoutResponse from(Order order, PaymentSummary payment) {
    List<OrderLineItemResponse> items =
        order.getItems().stream().map(OrderLineItemResponse::from).toList();
    return new CheckoutResponse(
        order.getId(),
        order.getStatus(),
        order.getOrderDate(),
        order.getTotalAmount(),
        items,
        payment);
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public List<OrderLineItemResponse> getItems() {
    return items;
  }

  public void setItems(List<OrderLineItemResponse> items) {
    this.items = items;
  }

  public PaymentSummary getPayment() {
    return payment;
  }

  public void setPayment(PaymentSummary payment) {
    this.payment = payment;
  }
}

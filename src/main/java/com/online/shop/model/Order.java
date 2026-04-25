package com.online.shop.model;

import com.online.shop.core.observer.OrderObserver;
import com.online.shop.core.state.OrderState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "orders",
    indexes = {
      @Index(name = "idx_order_customer", columnList = "customer_id"),
      @Index(name = "idx_order_status", columnList = "status")
    })
public class Order {

  @Id
  @Column(length = 36)
  private String id;

  @Column(name = "customer_id", nullable = false, length = 36)
  private String customerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private OrderStatus status = OrderStatus.PLACED;

  @Column(name = "total_amount", nullable = false)
  private BigDecimal totalAmount;

  @Column(name = "order_date", nullable = false)
  private LocalDateTime orderDate;

  @Column(name = "shipping_street", length = 255)
  private String shippingStreet = "";

  @Column(name = "shipping_city", length = 100)
  private String shippingCity = "";

  @Column(name = "shipping_state", length = 100)
  private String shippingState = "";

  @Column(name = "shipping_zip_code", length = 20)
  private String shippingZipCode = "";

  @OneToMany(
      mappedBy = "order",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<OrderLineItem> items = new ArrayList<>();

  @Transient private OrderState currentState;

  @Transient private List<OrderObserver> observers = new ArrayList<>();

  protected Order() {}

  public Order(
      String customerId,
      List<OrderLineItem> items,
      String shippingStreet,
      String shippingCity,
      String shippingState,
      String shippingZipCode,
      BigDecimal totalAmount) {
    this(
        null,
        customerId,
        items,
        shippingStreet,
        shippingCity,
        shippingState,
        shippingZipCode,
        totalAmount);
  }

  public Order(
      String id,
      String customerId,
      List<OrderLineItem> items,
      String shippingStreet,
      String shippingCity,
      String shippingState,
      String shippingZipCode,
      BigDecimal totalAmount) {
    this.id = id != null ? id : UUID.randomUUID().toString();
    this.customerId = customerId;
    this.shippingStreet = shippingStreet == null ? "" : shippingStreet;
    this.shippingCity = shippingCity == null ? "" : shippingCity;
    this.shippingState = shippingState == null ? "" : shippingState;
    this.shippingZipCode = shippingZipCode == null ? "" : shippingZipCode;
    this.totalAmount = totalAmount;
    this.orderDate = LocalDateTime.now();
    this.status = OrderStatus.PLACED;
    if (items != null) {
      for (OrderLineItem li : items) {
        addItem(li);
      }
    }
  }

  public void addItem(OrderLineItem item) {
    item.setOrder(this);
    items.add(item);
  }

  public String getId() {
    return id;
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

  public String getShippingStreet() {
    return shippingStreet;
  }

  public String getShippingCity() {
    return shippingCity;
  }

  public String getShippingState() {
    return shippingState;
  }

  public String getShippingZipCode() {
    return shippingZipCode;
  }

  public List<OrderLineItem> getItems() {
    return items;
  }

  public void setState(OrderState state) {
    this.currentState = state;
  }

  public OrderState getState() {
    return currentState;
  }

  public void addObserver(OrderObserver observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
  }

  public void removeObserver(OrderObserver observer) {
    observers.remove(observer);
  }

  public void notifyObservers() {
    for (OrderObserver observer : observers) {
      observer.update(this);
    }
  }

  public List<OrderObserver> getObservers() {
    return observers;
  }

  public void shipOrder() {
    requireState();
    currentState.shipOrder(this);
  }

  public void deliverOrder() {
    requireState();
    currentState.deliverOrder(this);
  }

  public void cancelOrder() {
    requireState();
    currentState.cancelOrder(this);
  }

  private void requireState() {
    if (currentState == null) {
      throw new IllegalStateException("Order state not initialised");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Order other)) return false;
    return Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Order(id=" + id + ", status=" + status + ", total=" + totalAmount + ")";
  }
}

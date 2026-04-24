package com.online.shop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "order_line_items")
public class OrderLineItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(name = "product_id", nullable = false, length = 36)
  private String productId;

  @Column(name = "product_name", length = 200)
  private String productName;

  @Column(nullable = false)
  private int quantity;

  @Column(name = "price_at_purchase", nullable = false)
  private BigDecimal priceAtPurchase;

  protected OrderLineItem() {}

  public OrderLineItem(
      String productId, String productName, int quantity, BigDecimal priceAtPurchase) {
    this.productId = productId;
    this.productName = productName;
    this.quantity = quantity;
    this.priceAtPurchase = priceAtPurchase;
  }

  public Long getId() {
    return id;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public String getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getPriceAtPurchase() {
    return priceAtPurchase;
  }

  public BigDecimal getLineTotal() {
    return priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
  }

  @Override
  public String toString() {
    return "OrderLineItem(productId="
        + productId
        + ", productName="
        + productName
        + ", quantity="
        + quantity
        + ", priceAtPurchase="
        + priceAtPurchase
        + ")";
  }
}

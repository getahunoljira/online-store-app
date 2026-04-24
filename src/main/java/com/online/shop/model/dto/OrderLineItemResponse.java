package com.online.shop.model.dto;

import com.online.shop.model.OrderLineItem;
import java.math.BigDecimal;

public class OrderLineItemResponse {

  private String productId;
  private String productName;
  private int quantity;
  private BigDecimal priceAtPurchase;
  private BigDecimal lineTotal;

  public OrderLineItemResponse() {}

  public OrderLineItemResponse(
      String productId,
      String productName,
      int quantity,
      BigDecimal priceAtPurchase,
      BigDecimal lineTotal) {
    this.productId = productId;
    this.productName = productName;
    this.quantity = quantity;
    this.priceAtPurchase = priceAtPurchase;
    this.lineTotal = lineTotal;
  }

  public static OrderLineItemResponse from(OrderLineItem li) {
    return new OrderLineItemResponse(
        li.getProductId(),
        li.getProductName(),
        li.getQuantity(),
        li.getPriceAtPurchase(),
        li.getLineTotal());
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getPriceAtPurchase() {
    return priceAtPurchase;
  }

  public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
    this.priceAtPurchase = priceAtPurchase;
  }

  public BigDecimal getLineTotal() {
    return lineTotal;
  }

  public void setLineTotal(BigDecimal lineTotal) {
    this.lineTotal = lineTotal;
  }
}

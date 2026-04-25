package com.online.shop.model.dto;

import com.online.shop.service.cart.ShoppingCartItem;
import java.math.BigDecimal;

public class CartItemResponse {

  private String productId;
  private String productName;
  private int quantity;
  private BigDecimal unitPrice;
  private BigDecimal lineTotal;

  public CartItemResponse() {}

  public CartItemResponse(
      String productId,
      String productName,
      int quantity,
      BigDecimal unitPrice,
      BigDecimal lineTotal) {
    this.productId = productId;
    this.productName = productName;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.lineTotal = lineTotal;
  }

  public static CartItemResponse from(ShoppingCartItem item) {
    return new CartItemResponse(
        item.getProduct().getId(),
        item.getProduct().getName(),
        item.getQuantity(),
        item.getProduct().getPrice(),
        item.getPrice());
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

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public BigDecimal getLineTotal() {
    return lineTotal;
  }

  public void setLineTotal(BigDecimal lineTotal) {
    this.lineTotal = lineTotal;
  }
}

package com.online.shop.domain.patterns;

import java.math.BigDecimal;

public class ShoppingCartItem {

  private final BaseProduct product;
  private int quantity;

  public ShoppingCartItem(BaseProduct product) {
    this(product, 1);
  }

  public ShoppingCartItem(BaseProduct product, int quantity) {
    this.product = product;
    this.quantity = quantity;
  }

  public BaseProduct getProduct() {
    return product;
  }

  public int getQuantity() {
    return quantity;
  }

  public void incrementQuantity(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Increment amount must be positive");
    }
    this.quantity += amount;
  }

  public void setQuantity(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    this.quantity = quantity;
  }

  public BigDecimal getPrice() {
    return product.getPrice().multiply(BigDecimal.valueOf(quantity));
  }

  @Override
  public String toString() {
    return "ShoppingCartItem(product=" + product.getName() + ", quantity=" + quantity + ")";
  }
}

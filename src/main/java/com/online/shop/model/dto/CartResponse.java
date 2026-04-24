package com.online.shop.model.dto;

import com.online.shop.domain.patterns.ShoppingCart;
import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

  private String customerId;
  private List<CartItemResponse> items;
  private BigDecimal total;

  public CartResponse() {}

  public CartResponse(String customerId, List<CartItemResponse> items, BigDecimal total) {
    this.customerId = customerId;
    this.items = items;
    this.total = total;
  }

  public static CartResponse from(String customerId, ShoppingCart cart) {
    List<CartItemResponse> items =
        cart.getItems().values().stream().map(CartItemResponse::from).toList();
    return new CartResponse(customerId, items, cart.calculateTotal());
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public List<CartItemResponse> getItems() {
    return items;
  }

  public void setItems(List<CartItemResponse> items) {
    this.items = items;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }
}

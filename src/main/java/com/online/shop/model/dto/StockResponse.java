package com.online.shop.model.dto;

public class StockResponse {

  private final String productId;
  private final int stock;

  public StockResponse(String productId, int stock) {
    this.productId = productId;
    this.stock = stock;
  }

  public String getProductId() {
    return productId;
  }

  public int getStock() {
    return stock;
  }
}

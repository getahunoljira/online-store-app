package com.online.shop.model.dto;

import jakarta.validation.constraints.Positive;

public class StockUpdateRequest {

  @Positive private int quantity;

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}

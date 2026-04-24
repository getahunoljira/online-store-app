package com.online.shop.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OrderStatusUpdateRequest {

  @NotBlank
  @Pattern(regexp = "^(ship|deliver|cancel)$")
  private String action;

  public OrderStatusUpdateRequest() {}

  public OrderStatusUpdateRequest(String action) {
    this.action = action;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}

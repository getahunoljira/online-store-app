package com.online.shop.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ShippingAddressUpdateRequest {

  @NotNull @Valid private AddressDto shippingAddress;

  public AddressDto getShippingAddress() {
    return shippingAddress;
  }

  public void setShippingAddress(AddressDto shippingAddress) {
    this.shippingAddress = shippingAddress;
  }
}

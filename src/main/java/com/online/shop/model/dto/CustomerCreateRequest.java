package com.online.shop.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerCreateRequest {

  @NotBlank
  @Size(min = 1, max = 200)
  private String name;

  @NotBlank @Email private String email;

  @NotBlank
  @Size(min = 3, max = 100)
  private String username;

  @NotBlank
  @Size(min = 6)
  private String password;

  @Valid private AddressDto shippingAddress;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public AddressDto getShippingAddress() {
    return shippingAddress;
  }

  public void setShippingAddress(AddressDto shippingAddress) {
    this.shippingAddress = shippingAddress;
  }
}

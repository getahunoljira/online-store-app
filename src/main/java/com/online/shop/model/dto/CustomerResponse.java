package com.online.shop.model.dto;

import com.online.shop.model.Customer;

public class CustomerResponse {

  private String id;
  private String name;
  private String email;
  private AddressDto shippingAddress;

  public CustomerResponse() {}

  public CustomerResponse(String id, String name, String email, AddressDto shippingAddress) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.shippingAddress = shippingAddress;
  }

  public static CustomerResponse from(Customer customer) {
    return new CustomerResponse(
        customer.getId(),
        customer.getName(),
        customer.getEmail(),
        AddressDto.from(customer.getShippingAddress()));
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public AddressDto getShippingAddress() {
    return shippingAddress;
  }

  public void setShippingAddress(AddressDto shippingAddress) {
    this.shippingAddress = shippingAddress;
  }
}

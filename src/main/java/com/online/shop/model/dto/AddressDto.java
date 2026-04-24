package com.online.shop.model.dto;

import com.online.shop.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressDto {

  @NotBlank
  @Size(min = 1)
  private String street;

  @NotBlank
  @Size(min = 1)
  private String city;

  @NotBlank
  @Size(min = 1)
  private String state;

  @NotBlank
  @Size(min = 1)
  private String zipCode;

  public AddressDto() {}

  public AddressDto(String street, String city, String state, String zipCode) {
    this.street = street;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
  }

  public static AddressDto from(Address address) {
    if (address == null) {
      return null;
    }
    return new AddressDto(
        address.getStreet(), address.getCity(), address.getState(), address.getZipCode());
  }

  public Address toEntity() {
    return new Address(street, city, state, zipCode);
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }
}

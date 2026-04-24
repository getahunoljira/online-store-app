package com.online.shop.model.dto;

public class LoginResponse {

  private String customerId;
  private String name;
  private String email;
  private String message = "Login successful";

  public LoginResponse() {}

  public LoginResponse(String customerId, String name, String email) {
    this.customerId = customerId;
    this.name = name;
    this.email = email;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
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

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

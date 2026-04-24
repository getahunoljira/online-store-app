package com.online.shop.exception;

public class OrderNotFoundException extends RuntimeException {

  public OrderNotFoundException(String orderId) {
    super("Order " + orderId + " not found");
  }
}

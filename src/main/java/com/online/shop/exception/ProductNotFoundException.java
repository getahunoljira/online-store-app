package com.online.shop.exception;

public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException(String productId) {
    super("Product " + productId + " not found");
  }
}

package com.online.shop.exception;

public class PaymentNotFoundException extends RuntimeException {

  public PaymentNotFoundException(String paymentId) {
    super("Payment " + paymentId + " not found");
  }
}

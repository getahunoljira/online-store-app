package com.online.shop.exception;

public class PaymentFailedException extends RuntimeException {

  private final String paymentId;

  public PaymentFailedException(String paymentId, String message) {
    super(message);
    this.paymentId = paymentId;
  }

  public String getPaymentId() {
    return paymentId;
  }
}

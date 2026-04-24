package com.online.shop.service;

import com.online.shop.model.Payment;

public class PaymentResult {

  private final Payment payment;
  private final boolean succeeded;
  private final String transactionId;
  private final String message;

  public PaymentResult(Payment payment, boolean succeeded, String transactionId, String message) {
    this.payment = payment;
    this.succeeded = succeeded;
    this.transactionId = transactionId;
    this.message = message;
  }

  public Payment getPayment() {
    return payment;
  }

  public boolean isSucceeded() {
    return succeeded;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public String getMessage() {
    return message;
  }
}

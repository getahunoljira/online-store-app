package com.online.shop.model.dto;

import com.online.shop.model.PaymentStatus;
import java.math.BigDecimal;

public class PaymentSummary {

  private String paymentId;
  private PaymentStatus status;
  private String transactionId;
  private BigDecimal amount;
  private String currency;
  private String method;
  private String message;

  public PaymentSummary() {}

  public PaymentSummary(
      String paymentId,
      PaymentStatus status,
      String transactionId,
      BigDecimal amount,
      String currency,
      String method,
      String message) {
    this.paymentId = paymentId;
    this.status = status;
    this.transactionId = transactionId;
    this.amount = amount;
    this.currency = currency;
    this.method = method;
    this.message = message;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

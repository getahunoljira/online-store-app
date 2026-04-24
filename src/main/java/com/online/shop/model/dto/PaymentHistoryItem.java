package com.online.shop.model.dto;

import com.online.shop.model.Payment;
import com.online.shop.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentHistoryItem {

  private String paymentId;
  private String orderId;
  private BigDecimal amount;
  private String currency;
  private String method;
  private PaymentStatus status;
  private String transactionId;
  private LocalDateTime createdAt;

  public PaymentHistoryItem() {}

  public PaymentHistoryItem(
      String paymentId,
      String orderId,
      BigDecimal amount,
      String currency,
      String method,
      PaymentStatus status,
      String transactionId,
      LocalDateTime createdAt) {
    this.paymentId = paymentId;
    this.orderId = orderId;
    this.amount = amount;
    this.currency = currency;
    this.method = method;
    this.status = status;
    this.transactionId = transactionId;
    this.createdAt = createdAt;
  }

  public static PaymentHistoryItem from(Payment p) {
    return new PaymentHistoryItem(
        p.getId(),
        p.getOrderId(),
        p.getAmount(),
        p.getCurrency(),
        p.getMethod(),
        p.getStatus(),
        p.getTransactionId(),
        p.getCreatedAt());
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}

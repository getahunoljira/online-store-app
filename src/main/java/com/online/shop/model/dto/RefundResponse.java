package com.online.shop.model.dto;

import com.online.shop.model.PaymentStatus;

public class RefundResponse {

  private String paymentId;
  private PaymentStatus status;
  private String refundTransactionId;
  private String message;

  public RefundResponse() {}

  public RefundResponse(
      String paymentId, PaymentStatus status, String refundTransactionId, String message) {
    this.paymentId = paymentId;
    this.status = status;
    this.refundTransactionId = refundTransactionId;
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

  public String getRefundTransactionId() {
    return refundTransactionId;
  }

  public void setRefundTransactionId(String refundTransactionId) {
    this.refundTransactionId = refundTransactionId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

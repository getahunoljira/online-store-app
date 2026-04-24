package com.online.shop.payment;

import java.math.BigDecimal;

public class GatewayRefundResult {

  private String refundId;
  private String status;
  private BigDecimal amountRefunded;
  private String message;

  public GatewayRefundResult() {}

  public GatewayRefundResult(
      String refundId, String status, BigDecimal amountRefunded, String message) {
    this.refundId = refundId;
    this.status = status;
    this.amountRefunded = amountRefunded;
    this.message = message;
  }

  public String getRefundId() {
    return refundId;
  }

  public void setRefundId(String refundId) {
    this.refundId = refundId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public BigDecimal getAmountRefunded() {
    return amountRefunded;
  }

  public void setAmountRefunded(BigDecimal amountRefunded) {
    this.amountRefunded = amountRefunded;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

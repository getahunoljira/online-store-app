package com.online.shop.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class GatewayChargeResult {

  private String transactionId;
  private String status;
  private BigDecimal amountCharged;
  private String currency;
  private String message;
  private Map<String, Object> rawResponse = new HashMap<>();

  public GatewayChargeResult() {}

  public GatewayChargeResult(
      String transactionId,
      String status,
      BigDecimal amountCharged,
      String currency,
      String message) {
    this.transactionId = transactionId;
    this.status = status;
    this.amountCharged = amountCharged;
    this.currency = currency;
    this.message = message;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public BigDecimal getAmountCharged() {
    return amountCharged;
  }

  public void setAmountCharged(BigDecimal amountCharged) {
    this.amountCharged = amountCharged;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Map<String, Object> getRawResponse() {
    return rawResponse;
  }

  public void setRawResponse(Map<String, Object> rawResponse) {
    this.rawResponse = rawResponse == null ? new HashMap<>() : rawResponse;
  }
}

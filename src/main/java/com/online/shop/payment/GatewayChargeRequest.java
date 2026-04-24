package com.online.shop.payment;

import java.math.BigDecimal;

public class GatewayChargeRequest {

  private BigDecimal amount;
  private String currency;
  private String methodType;
  private String idempotencyKey;
  private String cardNumber;
  private Integer cardExpiryMonth;
  private Integer cardExpiryYear;
  private String cardCvv;
  private String cardHolderName;
  private String upiId;
  private String description = "";

  public GatewayChargeRequest() {}

  public GatewayChargeRequest(
      BigDecimal amount, String currency, String methodType, String idempotencyKey) {
    this.amount = amount;
    this.currency = currency;
    this.methodType = methodType;
    this.idempotencyKey = idempotencyKey;
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

  public String getMethodType() {
    return methodType;
  }

  public void setMethodType(String methodType) {
    this.methodType = methodType;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public Integer getCardExpiryMonth() {
    return cardExpiryMonth;
  }

  public void setCardExpiryMonth(Integer cardExpiryMonth) {
    this.cardExpiryMonth = cardExpiryMonth;
  }

  public Integer getCardExpiryYear() {
    return cardExpiryYear;
  }

  public void setCardExpiryYear(Integer cardExpiryYear) {
    this.cardExpiryYear = cardExpiryYear;
  }

  public String getCardCvv() {
    return cardCvv;
  }

  public void setCardCvv(String cardCvv) {
    this.cardCvv = cardCvv;
  }

  public String getCardHolderName() {
    return cardHolderName;
  }

  public void setCardHolderName(String cardHolderName) {
    this.cardHolderName = cardHolderName;
  }

  public String getUpiId() {
    return upiId;
  }

  public void setUpiId(String upiId) {
    this.upiId = upiId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description == null ? "" : description;
  }
}

package com.online.shop.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CheckoutRequest {

  @NotBlank
  @Pattern(regexp = "^(credit_card|upi)$", message = "payment_method must be 'credit_card' or 'upi'")
  private String paymentMethod;

  @Size(min = 3, max = 3)
  private String currency = "USD";

  @Size(min = 12, max = 19)
  private String cardNumber;

  @Min(1)
  @Max(12)
  private Integer cardExpiryMonth;

  @Min(2024)
  private Integer cardExpiryYear;

  @Size(min = 3, max = 4)
  private String cardCvv;

  private String cardHolderName;

  private String upiId;

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
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

  @AssertTrue(message = "card_number is required for credit_card payment")
  @JsonIgnore
  public boolean isCreditCardDetailsPresent() {
    if (!"credit_card".equals(paymentMethod)) {
      return true;
    }
    return cardNumber != null && !cardNumber.isBlank();
  }

  @AssertTrue(message = "upi_id is required for upi payment")
  @JsonIgnore
  public boolean isUpiDetailsPresent() {
    if (!"upi".equals(paymentMethod)) {
      return true;
    }
    return upiId != null && !upiId.isBlank();
  }
}

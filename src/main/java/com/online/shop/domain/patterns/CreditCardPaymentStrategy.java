package com.online.shop.domain.patterns;

import com.online.shop.payment.GatewayChargeRequest;
import java.math.BigDecimal;

public class CreditCardPaymentStrategy implements PaymentStrategy {

  private final String cardNumber;
  private final Integer expiryMonth;
  private final Integer expiryYear;
  private final String cvv;
  private final String cardHolderName;

  public CreditCardPaymentStrategy(String cardNumber) {
    this(cardNumber, null, null, null, null);
  }

  public CreditCardPaymentStrategy(
      String cardNumber,
      Integer expiryMonth,
      Integer expiryYear,
      String cvv,
      String cardHolderName) {
    this.cardNumber = cardNumber;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.cvv = cvv;
    this.cardHolderName = cardHolderName;
  }

  @Override
  public GatewayChargeRequest buildChargeRequest(
      BigDecimal amount, String currency, String idempotencyKey, String description) {
    GatewayChargeRequest req =
        new GatewayChargeRequest(amount, currency, "credit_card", idempotencyKey);
    req.setCardNumber(cardNumber);
    req.setCardExpiryMonth(expiryMonth);
    req.setCardExpiryYear(expiryYear);
    req.setCardCvv(cvv);
    req.setCardHolderName(cardHolderName);
    req.setDescription(description);
    return req;
  }

  public String getCardNumber() {
    return cardNumber;
  }
}

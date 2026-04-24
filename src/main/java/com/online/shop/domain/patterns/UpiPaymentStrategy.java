package com.online.shop.domain.patterns;

import com.online.shop.payment.GatewayChargeRequest;
import java.math.BigDecimal;

public class UpiPaymentStrategy implements PaymentStrategy {

  private final String upiId;

  public UpiPaymentStrategy(String upiId) {
    this.upiId = upiId;
  }

  @Override
  public GatewayChargeRequest buildChargeRequest(
      BigDecimal amount, String currency, String idempotencyKey, String description) {
    GatewayChargeRequest req = new GatewayChargeRequest(amount, currency, "upi", idempotencyKey);
    req.setUpiId(upiId);
    req.setDescription(description);
    return req;
  }

  public String getUpiId() {
    return upiId;
  }
}

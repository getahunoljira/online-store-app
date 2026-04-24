package com.online.shop.domain.patterns;

import com.online.shop.payment.GatewayChargeRequest;
import com.online.shop.payment.GatewayChargeResult;
import com.online.shop.payment.PaymentGatewayClient;
import java.math.BigDecimal;

public interface PaymentStrategy {

  GatewayChargeRequest buildChargeRequest(
      BigDecimal amount, String currency, String idempotencyKey, String description);

  default GatewayChargeResult pay(
      PaymentGatewayClient gateway,
      BigDecimal amount,
      String currency,
      String idempotencyKey) {
    GatewayChargeRequest request = buildChargeRequest(amount, currency, idempotencyKey, "");
    return gateway.charge(request);
  }
}

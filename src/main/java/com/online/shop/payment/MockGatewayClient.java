package com.online.shop.payment;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class MockGatewayClient implements IPaymentGateway {

  private static final Set<String> DECLINE_CARDS =
      Set.of("4000000000000002", "4000000000000069", "4000000000000127");

  @Override
  public GatewayChargeResult charge(GatewayChargeRequest request) {
    if ("credit_card".equals(request.getMethodType())) {
      if (request.getCardNumber() != null && DECLINE_CARDS.contains(request.getCardNumber())) {
        return new GatewayChargeResult(
            "", "failed", BigDecimal.ZERO, request.getCurrency(), "Your card was declined");
      }
      if (request.getCardNumber() == null || request.getCardNumber().length() < 12) {
        return new GatewayChargeResult(
            "", "failed", BigDecimal.ZERO, request.getCurrency(), "Invalid card number");
      }
    } else if ("upi".equals(request.getMethodType())) {
      if (request.getUpiId() == null || !request.getUpiId().contains("@")) {
        return new GatewayChargeResult(
            "", "failed", BigDecimal.ZERO, request.getCurrency(), "Invalid UPI ID");
      }
    }

    String txnId = "mock_txn_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    return new GatewayChargeResult(
        txnId, "succeeded", request.getAmount(), request.getCurrency(), "Payment successful");
  }

  @Override
  public GatewayRefundResult refund(String transactionId, BigDecimal amount) {
    if (transactionId == null || transactionId.isEmpty()) {
      return new GatewayRefundResult(
          "", "failed", BigDecimal.ZERO, "Invalid transaction ID");
    }
    String refundId =
        "mock_refund_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    return new GatewayRefundResult(
        refundId, "succeeded", amount == null ? BigDecimal.ZERO : amount, "Refund successful");
  }
}

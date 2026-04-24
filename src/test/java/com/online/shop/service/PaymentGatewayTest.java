package com.online.shop.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.online.shop.payment.GatewayChargeRequest;
import com.online.shop.payment.GatewayChargeResult;
import com.online.shop.payment.GatewayRefundResult;
import com.online.shop.payment.MockGatewayClient;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PaymentGatewayTest {

  private GatewayChargeRequest makeRequest(String cardNumber, String method) {
    GatewayChargeRequest req =
        new GatewayChargeRequest(new BigDecimal("99.99"), "USD", method, "test-key-123");
    req.setCardNumber(cardNumber);
    if ("upi".equals(method)) {
      req.setUpiId("user@bank");
    }
    return req;
  }

  @Test
  void validCardSucceeds() {
    MockGatewayClient gw = new MockGatewayClient();
    GatewayChargeResult result = gw.charge(makeRequest("4111111111111111", "credit_card"));
    assertThat(result.getStatus()).isEqualTo("succeeded");
    assertThat(result.getTransactionId()).startsWith("mock_txn_");
    assertThat(result.getAmountCharged()).isEqualByComparingTo(new BigDecimal("99.99"));
  }

  @Test
  void declineCardFails() {
    MockGatewayClient gw = new MockGatewayClient();
    GatewayChargeResult result = gw.charge(makeRequest("4000000000000002", "credit_card"));
    assertThat(result.getStatus()).isEqualTo("failed");
    assertThat(result.getTransactionId()).isEqualTo("");
  }

  @Test
  void shortCardNumberFails() {
    MockGatewayClient gw = new MockGatewayClient();
    GatewayChargeResult result = gw.charge(makeRequest("123", "credit_card"));
    assertThat(result.getStatus()).isEqualTo("failed");
  }

  @Test
  void validUpiSucceeds() {
    MockGatewayClient gw = new MockGatewayClient();
    GatewayChargeResult result = gw.charge(makeRequest(null, "upi"));
    assertThat(result.getStatus()).isEqualTo("succeeded");
  }

  @Test
  void invalidUpiFails() {
    MockGatewayClient gw = new MockGatewayClient();
    GatewayChargeRequest req =
        new GatewayChargeRequest(new BigDecimal("10.0"), "USD", "upi", "k");
    req.setUpiId("invalid");
    GatewayChargeResult result = gw.charge(req);
    assertThat(result.getStatus()).isEqualTo("failed");
  }

  @Test
  void refundSucceeds() {
    MockGatewayClient gw = new MockGatewayClient();
    GatewayRefundResult result = gw.refund("mock_txn_abc123", new BigDecimal("50.0"));
    assertThat(result.getStatus()).isEqualTo("succeeded");
    assertThat(result.getRefundId()).startsWith("mock_refund_");
    assertThat(result.getAmountRefunded()).isEqualByComparingTo(new BigDecimal("50.0"));
  }

  @Test
  void refundEmptyTransactionFails() {
    MockGatewayClient gw = new MockGatewayClient();
    GatewayRefundResult result = gw.refund("", null);
    assertThat(result.getStatus()).isEqualTo("failed");
  }
}

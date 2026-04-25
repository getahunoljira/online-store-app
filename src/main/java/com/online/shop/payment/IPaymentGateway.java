package com.online.shop.payment;

import java.math.BigDecimal;

public interface IPaymentGateway {

  GatewayChargeResult charge(GatewayChargeRequest request);

  GatewayRefundResult refund(String transactionId, BigDecimal amount);
}

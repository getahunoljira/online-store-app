package com.online.shop.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentGatewayConfig {

  @Bean
  public PaymentGatewayClient paymentGatewayClient(
      @Value("${payment.gateway:mock}") String gateway,
      @Value("${stripe.api-key:}") String stripeApiKey) {
    if ("stripe".equalsIgnoreCase(gateway)) {
      if (stripeApiKey == null || stripeApiKey.isEmpty()) {
        throw new IllegalStateException(
            "stripe.api-key must be set when payment.gateway=stripe");
      }
      return new StripeGatewayClient(stripeApiKey);
    }
    return new MockGatewayClient();
  }
}

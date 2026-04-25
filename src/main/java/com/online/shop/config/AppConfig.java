package com.online.shop.config;

import com.online.shop.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.online.shop.payment.IPaymentGateway;
import com.online.shop.payment.MockGatewayClient;
import com.online.shop.payment.StripeGatewayClient;


@Configuration
public class AppConfig {

  @Bean
  public PasswordHasher passwordHasher() {
    return new PasswordHasher();
  }

  @Bean
  public IPaymentGateway ipaymentGateway(
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

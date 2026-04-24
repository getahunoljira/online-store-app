package com.online.shop.config;

import com.online.shop.util.PasswordHasher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public PasswordHasher passwordHasher() {
    return new PasswordHasher();
  }
}

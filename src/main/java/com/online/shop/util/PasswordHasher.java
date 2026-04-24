package com.online.shop.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public String hash(String plain) {
    return encoder.encode(plain);
  }

  public boolean verify(String plain, String hashed) {
    if (hashed == null || hashed.isEmpty()) {
      return false;
    }
    return encoder.matches(plain, hashed);
  }
}

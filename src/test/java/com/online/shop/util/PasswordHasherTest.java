package com.online.shop.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordHasherTest {

  private final PasswordHasher hasher = new PasswordHasher();

  @Test
  void hashProducesBcryptString() {
    String hashed = hasher.hash("secret123");
    assertThat(hashed).isNotEqualTo("secret123");
    assertThat(hashed).startsWith("$2");
  }

  @Test
  void verifyMatchesCorrectPassword() {
    String hashed = hasher.hash("secret123");
    assertThat(hasher.verify("secret123", hashed)).isTrue();
  }

  @Test
  void verifyRejectsWrongPassword() {
    String hashed = hasher.hash("secret123");
    assertThat(hasher.verify("wrong", hashed)).isFalse();
  }

  @Test
  void verifyRejectsNullHash() {
    assertThat(hasher.verify("secret123", null)).isFalse();
  }

  @Test
  void verifyRejectsEmptyHash() {
    assertThat(hasher.verify("secret123", "")).isFalse();
  }

  @Test
  void hashIsNonDeterministic() {
    String h1 = hasher.hash("secret123");
    String h2 = hasher.hash("secret123");
    assertThat(h1).isNotEqualTo(h2);
  }
}

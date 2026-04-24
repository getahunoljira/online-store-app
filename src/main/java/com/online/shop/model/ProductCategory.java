package com.online.shop.model;

public enum ProductCategory {
  CLOTHING,
  HOME_GOODS,
  GROCERY,
  ELECTRONICS,
  BOOKS;

  public static ProductCategory valueOfIgnoreCase(String value) {
    for (ProductCategory c : values()) {
      if (c.name().equalsIgnoreCase(value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("No ProductCategory with value '" + value + "'");
  }
}

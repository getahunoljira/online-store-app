package com.online.shop.model.dto;

import com.online.shop.service.product.BaseProduct;
import com.online.shop.model.ProductCategory;
import java.math.BigDecimal;

public class ProductResponse {

  private final String id;
  private final String name;
  private final BigDecimal price;
  private final String description;
  private final ProductCategory category;

  public ProductResponse(
      String id, String name, BigDecimal price, String description, ProductCategory category) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.description = description;
    this.category = category;
  }

  public static ProductResponse from(BaseProduct p) {
    return new ProductResponse(
        p.getId(), p.getName(), p.getPrice(), p.getDescription(), p.getCategory());
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getDescription() {
    return description;
  }

  public ProductCategory getCategory() {
    return category;
  }
}

package com.online.shop.model.dto;

import com.online.shop.model.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ProductCreateRequest {

  @NotBlank
  @Size(min = 1, max = 200)
  private String name;

  @NotNull
  @DecimalMin(value = "0", inclusive = false)
  private BigDecimal price;

  @Size(max = 1000)
  private String description = "";

  @NotNull private ProductCategory category;

  @PositiveOrZero private int initialStock = 0;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProductCategory getCategory() {
    return category;
  }

  public void setCategory(ProductCategory category) {
    this.category = category;
  }

  public int getInitialStock() {
    return initialStock;
  }

  public void setInitialStock(int initialStock) {
    this.initialStock = initialStock;
  }
}

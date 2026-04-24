package com.online.shop.domain.patterns;

import com.online.shop.model.ProductCategory;
import java.math.BigDecimal;

public abstract class ProductDecorator implements BaseProduct {

  public static final BigDecimal GIFT_WRAP_COST = new BigDecimal("5.0");

  protected final BaseProduct decoratedProduct;

  protected ProductDecorator(BaseProduct decoratedProduct) {
    this.decoratedProduct = decoratedProduct;
  }

  @Override
  public String getId() {
    return decoratedProduct.getId();
  }

  @Override
  public String getName() {
    return decoratedProduct.getName();
  }

  @Override
  public BigDecimal getPrice() {
    return decoratedProduct.getPrice();
  }

  @Override
  public ProductCategory getCategory() {
    return decoratedProduct.getCategory();
  }

  @Override
  public String getDescription() {
    return decoratedProduct.getDescription();
  }
}

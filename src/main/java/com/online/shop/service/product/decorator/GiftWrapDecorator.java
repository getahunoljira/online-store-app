package com.online.shop.service.product.decorator;

import com.online.shop.service.product.BaseProduct;
import java.math.BigDecimal;

public class GiftWrapDecorator extends ProductDecorator {

  public static final BigDecimal GIFT_WRAP_COST = new BigDecimal("5.0");

  public GiftWrapDecorator(BaseProduct decoratedProduct) {
    super(decoratedProduct);
  }

  @Override
  public String getDescription() {
    return decoratedProduct.getDescription() + " [Gift Wrapped]";
  }

  @Override
  public BigDecimal getPrice() {
    return decoratedProduct.getPrice().add(GIFT_WRAP_COST);
  }
}

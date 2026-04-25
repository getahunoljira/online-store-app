package com.online.shop.service.product;

import com.online.shop.model.ProductCategory;
import java.math.BigDecimal;

public interface BaseProduct {
  String getId();

  String getName();

  BigDecimal getPrice();

  ProductCategory getCategory();

  String getDescription();
}

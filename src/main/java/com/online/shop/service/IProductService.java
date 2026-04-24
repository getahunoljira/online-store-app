package com.online.shop.service;

import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import java.math.BigDecimal;
import java.util.Optional;

public interface IProductService {

  Product create(
      String name,
      BigDecimal price,
      String description,
      ProductCategory category,
      int initialStock);

  Optional<Product> getById(String id);

  Product requireById(String id);

  void delete(String id);
}

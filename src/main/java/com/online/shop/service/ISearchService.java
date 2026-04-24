package com.online.shop.service;

import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import java.util.List;

public interface ISearchService {

  List<Product> searchByCategory(ProductCategory category);

  List<Product> searchByName(String name);

  List<Product> getAllProducts();
}

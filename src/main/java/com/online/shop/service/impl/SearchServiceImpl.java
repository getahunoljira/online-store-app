package com.online.shop.service.impl;

import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import com.online.shop.repository.ProductRepository;
import com.online.shop.service.ISearchService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements ISearchService {

  private final ProductRepository productRepository;

  public SearchServiceImpl(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public List<Product> searchByCategory(ProductCategory category) {
    return productRepository.findByCategory(category);
  }

  @Override
  public List<Product> searchByName(String name) {
    if (name == null || name.trim().isEmpty()) {
      return List.of();
    }
    return productRepository.findByNameContainingIgnoreCase(name.trim());
  }

  @Override
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }
}

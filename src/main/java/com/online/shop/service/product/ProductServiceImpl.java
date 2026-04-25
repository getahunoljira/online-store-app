package com.online.shop.service.product;

import com.online.shop.exception.ProductNotFoundException;
import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import com.online.shop.repository.ProductRepository;
import com.online.shop.service.product.IProductService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements IProductService {

  private final ProductRepository productRepository;

  public ProductServiceImpl(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public Product create(
      String name,
      BigDecimal price,
      String description,
      ProductCategory category,
      int initialStock) {
    Product product = new Product(name, price, description, category);
    product.setStockQuantity(initialStock);
    return productRepository.save(product);
  }

  @Override
  public Optional<Product> getById(String id) {
    return productRepository.findById(id);
  }

  @Override
  public Product requireById(String id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }

  @Override
  public void delete(String id) {
    if (!productRepository.existsById(id)) {
      throw new ProductNotFoundException(id);
    }
    productRepository.deleteById(id);
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

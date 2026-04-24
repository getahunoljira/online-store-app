package com.online.shop.repository;

import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

  List<Product> findByCategory(ProductCategory category);

  List<Product> findByNameContainingIgnoreCase(String name);
}

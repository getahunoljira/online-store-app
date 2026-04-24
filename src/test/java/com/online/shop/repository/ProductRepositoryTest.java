package com.online.shop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class ProductRepositoryTest {

  @Autowired private ProductRepository repository;

  private Product makeProduct(String name, BigDecimal price, ProductCategory category) {
    Product p = new Product(name, price, "desc", category);
    return p;
  }

  private Product makeProduct() {
    return makeProduct("Widget", new BigDecimal("10.0"), ProductCategory.ELECTRONICS);
  }

  @Test
  void createAndGet() {
    Product p = makeProduct();
    p.setStockQuantity(5);
    Product saved = repository.save(p);

    Product fetched = repository.findById(saved.getId()).orElseThrow();
    assertThat(fetched.getName()).isEqualTo("Widget");
    assertThat(fetched.getStockQuantity()).isEqualTo(5);
  }

  @Test
  void findAll() {
    repository.save(makeProduct("A", new BigDecimal("1.0"), ProductCategory.BOOKS));
    repository.save(makeProduct("B", new BigDecimal("2.0"), ProductCategory.BOOKS));
    assertThat(repository.findAll()).hasSize(2);
  }

  @Test
  void findByCategory() {
    repository.save(makeProduct("Book", new BigDecimal("10"), ProductCategory.BOOKS));
    repository.save(makeProduct("Phone", new BigDecimal("100"), ProductCategory.ELECTRONICS));
    List<Product> result = repository.findByCategory(ProductCategory.BOOKS);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Book");
  }

  @Test
  void findByNameContainingIgnoreCase() {
    repository.save(makeProduct("Python Book", new BigDecimal("10"), ProductCategory.BOOKS));
    repository.save(makeProduct("Java Book", new BigDecimal("10"), ProductCategory.BOOKS));
    List<Product> result = repository.findByNameContainingIgnoreCase("python");
    assertThat(result).hasSize(1);
  }

  @Test
  void updateStock() {
    Product saved = repository.save(makeProduct());
    saved.setStockQuantity(25);
    repository.save(saved);
    assertThat(repository.findById(saved.getId()).orElseThrow().getStockQuantity())
        .isEqualTo(25);
  }

  @Test
  void deleteById() {
    Product saved = repository.save(makeProduct());
    repository.deleteById(saved.getId());
    assertThat(repository.findById(saved.getId())).isEmpty();
  }
}

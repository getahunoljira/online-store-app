package com.online.shop.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import com.online.shop.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock private ProductRepository productRepository;

  private IProductService service;

  @BeforeEach
  void setUp() {
    service = new ProductServiceImpl(productRepository);
  }

  private List<Product> makeProducts() {
    return List.of(
        new Product("p1", "Python Book", new BigDecimal("29.99"), "", ProductCategory.BOOKS),
        new Product("p2", "Laptop", new BigDecimal("999.99"), "", ProductCategory.ELECTRONICS),
        new Product("p3", "T-Shirt", new BigDecimal("19.99"), "", ProductCategory.CLOTHING));
  }

  @Test
  void searchByCategory() {
    List<Product> books =
        makeProducts().stream().filter(p -> p.getCategory() == ProductCategory.BOOKS).toList();
    when(productRepository.findByCategory(ProductCategory.BOOKS)).thenReturn(books);

    List<Product> result = service.searchByCategory(ProductCategory.BOOKS);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Python Book");
  }

  @Test
  void searchByName() {
    when(productRepository.findByNameContainingIgnoreCase("Python"))
        .thenReturn(List.of(makeProducts().get(0)));

    List<Product> result = service.searchByName("Python");

    verify(productRepository).findByNameContainingIgnoreCase("Python");
    assertThat(result).hasSize(1);
  }

  @Test
  void searchByEmptyNameReturnsEmpty() {
    List<Product> result = service.searchByName("  ");

    verify(productRepository, never()).findByNameContainingIgnoreCase(org.mockito.ArgumentMatchers.anyString());
    assertThat(result).isEmpty();
  }

  @Test
  void getAllProducts() {
    when(productRepository.findAll()).thenReturn(makeProducts());
    assertThat(service.getAllProducts()).hasSize(3);
  }
}

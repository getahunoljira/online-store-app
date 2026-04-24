package com.online.shop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import com.online.shop.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

  @Mock private ProductRepository productRepository;

  private IInventoryService service;

  @BeforeEach
  void setUp() {
    lenient().when(productRepository.findById(anyString())).thenReturn(Optional.empty());
    lenient().when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
    service = new com.online.shop.service.impl.InventoryServiceImpl(productRepository);
  }

  private Product makeProduct(String id) {
    return new Product(id, "Item", new BigDecimal("10.0"), "", ProductCategory.BOOKS);
  }

  @Test
  void addAndGetStock() {
    Product p = makeProduct("p1");
    service.addStock(p, 10);
    assertThat(service.getStock(p.getId())).isEqualTo(10);
  }

  @Test
  void addInvalidQuantityThrows() {
    assertThatThrownBy(() -> service.addStock(makeProduct("p1"), 0))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void isAvailableTrue() {
    Product p = makeProduct("p1");
    service.addStock(p, 5);
    assertThat(service.isAvailable(p.getId(), 5)).isTrue();
  }

  @Test
  void isAvailableFalse() {
    Product p = makeProduct("p1");
    service.addStock(p, 2);
    assertThat(service.isAvailable(p.getId(), 3)).isFalse();
  }

  @Test
  void addStockPersistsWhenProductExists() {
    Product persisted = makeProduct("p1");
    persisted.setStockQuantity(3);
    when(productRepository.findById("p1")).thenReturn(Optional.of(persisted));

    service.addStock(persisted, 4);

    assertThat(persisted.getStockQuantity()).isEqualTo(7);
  }
}

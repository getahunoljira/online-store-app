package com.online.shop.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.online.shop.domain.patterns.GiftWrapDecorator;
import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

  private Product makeProduct(String name, BigDecimal price, ProductCategory category) {
    return new Product(name, price, "desc", category);
  }

  private Product makeProduct() {
    return makeProduct("Test", new BigDecimal("10.0"), ProductCategory.ELECTRONICS);
  }

  @Test
  void productAttributes() {
    Product p = makeProduct("Laptop", new BigDecimal("999.99"), ProductCategory.ELECTRONICS);
    assertThat(p.getName()).isEqualTo("Laptop");
    assertThat(p.getPrice()).isEqualByComparingTo("999.99");
    assertThat(p.getCategory()).isEqualTo(ProductCategory.ELECTRONICS);
  }

  @Test
  void productHasUuidId() {
    Product p1 = makeProduct();
    Product p2 = makeProduct();
    assertThat(p1.getId()).isNotEqualTo(p2.getId());
    assertThat(p1.getId()).hasSize(36);
  }

  @Test
  void giftWrapDecoratorAddsCost() {
    Product p = makeProduct("X", new BigDecimal("50.0"), ProductCategory.ELECTRONICS);
    GiftWrapDecorator wrapped = new GiftWrapDecorator(p);
    assertThat(wrapped.getPrice()).isEqualByComparingTo("55.0");
    assertThat(wrapped.getDescription()).contains("[Gift Wrapped]");
  }

  @Test
  void giftWrapDecoratorPreservesIdAndCategory() {
    Product p = makeProduct("X", new BigDecimal("10.0"), ProductCategory.BOOKS);
    GiftWrapDecorator wrapped = new GiftWrapDecorator(p);
    assertThat(wrapped.getId()).isEqualTo(p.getId());
    assertThat(wrapped.getCategory()).isEqualTo(ProductCategory.BOOKS);
  }

  @Test
  void doubleWrap() {
    Product p = makeProduct("X", new BigDecimal("20.0"), ProductCategory.ELECTRONICS);
    GiftWrapDecorator wrapped = new GiftWrapDecorator(new GiftWrapDecorator(p));
    assertThat(wrapped.getPrice()).isEqualByComparingTo("30.0");
  }
}

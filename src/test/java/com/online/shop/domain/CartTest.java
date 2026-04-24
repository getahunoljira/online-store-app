package com.online.shop.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.online.shop.domain.patterns.ShoppingCart;
import com.online.shop.domain.patterns.ShoppingCartItem;
import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CartTest {

  private Product makeProduct(String name, BigDecimal price) {
    return new Product(name, price, "", ProductCategory.BOOKS);
  }

  private Product makeProduct() {
    return makeProduct("Item", new BigDecimal("10.0"));
  }

  @Test
  void addItemNew() {
    ShoppingCart cart = new ShoppingCart();
    Product p = makeProduct();
    cart.addItem(p, 2);
    assertThat(cart.getItems()).containsKey(p.getId());
    assertThat(cart.getItems().get(p.getId()).getQuantity()).isEqualTo(2);
  }

  @Test
  void addItemExistingIncrements() {
    ShoppingCart cart = new ShoppingCart();
    Product p = makeProduct();
    cart.addItem(p, 1);
    cart.addItem(p, 3);
    assertThat(cart.getItems().get(p.getId()).getQuantity()).isEqualTo(4);
  }

  @Test
  void removeItem() {
    ShoppingCart cart = new ShoppingCart();
    Product p = makeProduct();
    cart.addItem(p);
    cart.removeItem(p.getId());
    assertThat(cart.isEmpty()).isTrue();
  }

  @Test
  void calculateTotal() {
    ShoppingCart cart = new ShoppingCart();
    Product p1 = makeProduct("A", new BigDecimal("5.0"));
    Product p2 = makeProduct("B", new BigDecimal("3.0"));
    cart.addItem(p1, 2);
    cart.addItem(p2, 4);
    assertThat(cart.calculateTotal()).isEqualByComparingTo(new BigDecimal("22.0"));
  }

  @Test
  void clearCart() {
    ShoppingCart cart = new ShoppingCart();
    cart.addItem(makeProduct(), 5);
    cart.clearCart();
    assertThat(cart.isEmpty()).isTrue();
    assertThat(cart.calculateTotal()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  void cartItemGetPrice() {
    Product p = makeProduct("Item", new BigDecimal("7.5"));
    ShoppingCartItem item = new ShoppingCartItem(p, 3);
    assertThat(item.getPrice()).isEqualByComparingTo(new BigDecimal("22.5"));
  }

  @Test
  void cartItemIncrementQuantity() {
    ShoppingCartItem item = new ShoppingCartItem(makeProduct(), 2);
    item.incrementQuantity(3);
    assertThat(item.getQuantity()).isEqualTo(5);
  }

  @Test
  void cartItemIncrementInvalid() {
    ShoppingCartItem item = new ShoppingCartItem(makeProduct(), 1);
    assertThatThrownBy(() -> item.incrementQuantity(0))
        .isInstanceOf(IllegalArgumentException.class);
  }
}

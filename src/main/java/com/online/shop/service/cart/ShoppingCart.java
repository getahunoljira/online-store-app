package com.online.shop.service.cart;

import com.online.shop.service.product.BaseProduct;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShoppingCart {

  private final Map<String, ShoppingCartItem> items = new LinkedHashMap<>();

  public void addItem(BaseProduct product) {
    addItem(product, 1);
  }

  public void addItem(BaseProduct product, int quantity) {
    String pid = product.getId();
    ShoppingCartItem existing = items.get(pid);
    if (existing != null) {
      existing.incrementQuantity(quantity);
    } else {
      items.put(pid, new ShoppingCartItem(product, quantity));
    }
  }

  public void removeItem(String productId) {
    items.remove(productId);
  }

  public Map<String, ShoppingCartItem> getItems() {
    return new LinkedHashMap<>(items);
  }

  public void clearCart() {
    items.clear();
  }

  public BigDecimal calculateTotal() {
    return items.values().stream()
        .map(ShoppingCartItem::getPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public boolean isEmpty() {
    return items.isEmpty();
  }

  public int size() {
    return items.size();
  }
}

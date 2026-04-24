package com.online.shop.service;

import com.online.shop.domain.patterns.ShoppingCart;
import com.online.shop.model.Product;
import java.util.Optional;

public interface ICartService {

  ShoppingCart getCart(String customerId);

  void addItem(String customerId, String productId, int quantity);

  void removeItem(String customerId, String productId);

  void clearCart(String customerId);

  boolean productExists(String productId);

  Optional<Product> findProduct(String productId);
}

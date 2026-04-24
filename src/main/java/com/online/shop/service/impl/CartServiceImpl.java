package com.online.shop.service.impl;

import com.online.shop.domain.patterns.ShoppingCart;
import com.online.shop.model.Cart;
import com.online.shop.model.CartItem;
import com.online.shop.model.Product;
import com.online.shop.repository.CartRepository;
import com.online.shop.repository.ProductRepository;
import com.online.shop.service.ICartService;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements ICartService {

  private final CartRepository cartRepository;
  private final ProductRepository productRepository;

  public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository) {
    this.cartRepository = cartRepository;
    this.productRepository = productRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public ShoppingCart getCart(String customerId) {
    ShoppingCart shoppingCart = new ShoppingCart();
    Optional<Cart> cart = cartRepository.findByCustomerId(customerId);
    if (cart.isEmpty()) {
      return shoppingCart;
    }
    for (CartItem item : cart.get().getItems()) {
      productRepository
          .findById(item.getProductId())
          .ifPresent(product -> shoppingCart.addItem(product, item.getQuantity()));
    }
    return shoppingCart;
  }

  @Override
  @Transactional
  public void addItem(String customerId, String productId, int quantity) {
    Cart cart = cartRepository.findByCustomerId(customerId).orElseGet(() -> {
      Cart newCart = new Cart(customerId);
      return cartRepository.save(newCart);
    });

    CartItem existing =
        cart.getItems().stream()
            .filter(i -> i.getProductId().equals(productId))
            .findFirst()
            .orElse(null);

    if (existing != null) {
      existing.setQuantity(existing.getQuantity() + quantity);
    } else {
      cart.addItem(new CartItem(cart, productId, quantity));
    }
    cartRepository.save(cart);
  }

  @Override
  @Transactional
  public void removeItem(String customerId, String productId) {
    Optional<Cart> cartOpt = cartRepository.findByCustomerId(customerId);
    if (cartOpt.isEmpty()) {
      return;
    }
    Cart cart = cartOpt.get();
    CartItem toRemove =
        cart.getItems().stream()
            .filter(i -> i.getProductId().equals(productId))
            .findFirst()
            .orElse(null);
    if (toRemove != null) {
      cart.removeItem(toRemove);
      cartRepository.save(cart);
    }
  }

  @Override
  @Transactional
  public void clearCart(String customerId) {
    cartRepository
        .findByCustomerId(customerId)
        .ifPresent(
            cart -> {
              cart.getItems().clear();
              cartRepository.save(cart);
            });
  }

  @Override
  public boolean productExists(String productId) {
    return productRepository.existsById(productId);
  }

  @Override
  public Optional<Product> findProduct(String productId) {
    return productRepository.findById(productId);
  }
}

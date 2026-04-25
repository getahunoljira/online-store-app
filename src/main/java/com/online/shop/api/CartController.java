package com.online.shop.api;

import com.online.shop.model.dto.CartItemAddRequest;
import com.online.shop.model.dto.CartResponse;
import com.online.shop.exception.ProductNotFoundException;
import com.online.shop.service.cart.ShoppingCart;
import com.online.shop.service.cart.ICartService;
import com.online.shop.service.customer.ICustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers/{customerId}/cart")
public class CartController {

  private final ICartService cartService;
  private final ICustomerService customerService;

  public CartController(ICartService cartService, ICustomerService customerService) {
    this.cartService = cartService;
    this.customerService = customerService;
  }

  @GetMapping("/")
  public CartResponse getCart(@PathVariable String customerId) {
    customerService.requireById(customerId);
    ShoppingCart cart = cartService.getCart(customerId);
    return CartResponse.from(customerId, cart);
  }

  @PostMapping("/items")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addToCart(
      @PathVariable String customerId, @Valid @RequestBody CartItemAddRequest payload) {
    customerService.requireById(customerId);
    if (!cartService.productExists(payload.getProductId())) {
      throw new ProductNotFoundException(payload.getProductId());
    }
    cartService.addItem(customerId, payload.getProductId(), payload.getQuantity());
  }

  @DeleteMapping("/items/{productId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeFromCart(@PathVariable String customerId, @PathVariable String productId) {
    customerService.requireById(customerId);
    cartService.removeItem(customerId, productId);
  }

  @DeleteMapping("/")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void clearCart(@PathVariable String customerId) {
    customerService.requireById(customerId);
    cartService.clearCart(customerId);
  }
}

package com.online.shop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.online.shop.domain.patterns.ShoppingCart;
import com.online.shop.model.Account;
import com.online.shop.model.Address;
import com.online.shop.model.Customer;
import com.online.shop.model.Order;
import com.online.shop.model.OrderStatus;
import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import com.online.shop.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

  private Customer makeCustomer() {
    Account account = new Account("alice", "secret");
    Address address = new Address("1 Main St", "Springfield", "IL", "62701");
    return new Customer("cust-1", "Alice", "alice@example.com", account, address);
  }

  private ShoppingCart makeCartWithProduct() {
    ShoppingCart cart = new ShoppingCart();
    Product product =
        new Product("prod-1", "Widget", new BigDecimal("25.0"), "A widget", ProductCategory.ELECTRONICS);
    cart.addItem(product, 2);
    return cart;
  }

  @Test
  void createOrderSuccess() {
    OrderRepository mockRepo = mock(OrderRepository.class);
    IInventoryService mockInv = mock(IInventoryService.class);
    when(mockRepo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

    IOrderService svc = new com.online.shop.service.impl.OrderServiceImpl(mockRepo, mockInv, java.util.List.of());
    Order order = svc.createOrder(makeCustomer(), makeCartWithProduct());

    verify(mockInv, times(1)).updateStockForOrderList(anyList());
    verify(mockRepo, times(1)).save(any(Order.class));
    assertThat(order.getStatus()).isEqualTo(OrderStatus.PLACED);
    assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("50.0"));
  }

  @Test
  void createOrderEmptyCartThrows() {
    IOrderService svc = new com.online.shop.service.impl.OrderServiceImpl(mock(OrderRepository.class), mock(IInventoryService.class), java.util.List.of());
    assertThatThrownBy(() -> svc.createOrder(makeCustomer(), new ShoppingCart()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("empty");
  }

  @Test
  void createOrderNoAddressThrows() {
    IOrderService svc = new com.online.shop.service.impl.OrderServiceImpl(mock(OrderRepository.class), mock(IInventoryService.class), java.util.List.of());
    Customer customer = makeCustomer();
    customer.setShippingAddress(null);
    assertThatThrownBy(() -> svc.createOrder(customer, makeCartWithProduct()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("shipping address");
  }

  @Test
  void shipOrder() {
    OrderRepository mockRepo = mock(OrderRepository.class);
    Order persisted =
        new Order(
            "order-1",
            "cust-1",
            java.util.List.of(),
            "1 St",
            "City",
            "ST",
            "00000",
            new BigDecimal("50.0"));
    persisted.setStatus(OrderStatus.PLACED);
    when(mockRepo.findById(eq("order-1"))).thenReturn(Optional.of(persisted));
    when(mockRepo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

    IOrderService svc = new com.online.shop.service.impl.OrderServiceImpl(mockRepo, mock(IInventoryService.class), java.util.List.of());
    Order result = svc.shipOrder("order-1");

    assertThat(result.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    verify(mockRepo, times(1)).save(any(Order.class));
  }

  @Test
  void cancelOrder() {
    OrderRepository mockRepo = mock(OrderRepository.class);
    Order persisted =
        new Order(
            "order-1",
            "cust-1",
            java.util.List.of(),
            "1 St",
            "City",
            "ST",
            "00000",
            new BigDecimal("50.0"));
    persisted.setStatus(OrderStatus.PLACED);
    when(mockRepo.findById(eq("order-1"))).thenReturn(Optional.of(persisted));
    when(mockRepo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

    IOrderService svc = new com.online.shop.service.impl.OrderServiceImpl(mockRepo, mock(IInventoryService.class), java.util.List.of());
    Order result = svc.cancelOrder("order-1");

    assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
  }
}

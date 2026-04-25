package com.online.shop.core.observer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.online.shop.service.product.BaseProduct;
import com.online.shop.core.observer.InventoryReleaseObserver;
import com.online.shop.model.Order;
import com.online.shop.model.OrderLineItem;
import com.online.shop.model.OrderStatus;
import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import com.online.shop.repository.ProductRepository;
import com.online.shop.service.inventory.IInventoryService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InventoryReleaseObserverTest {

  private Order makeOrder(OrderStatus status, OrderLineItem... items) {
    Order order =
        new Order(
            "order-1",
            "cust-1",
            List.of(items),
            "1 St",
            "City",
            "ST",
            "00000",
            new BigDecimal("50.0"));
    order.setStatus(status);
    return order;
  }

  @Test
  void cancelledOrderRestocksEachLineItem() {
    IInventoryService inv = mock(IInventoryService.class);
    ProductRepository repo = mock(ProductRepository.class);
    Product widget = new Product("p-1", "Widget", new BigDecimal("10"), "", ProductCategory.ELECTRONICS);
    Product gadget = new Product("p-2", "Gadget", new BigDecimal("20"), "", ProductCategory.ELECTRONICS);
    when(repo.findById("p-1")).thenReturn(Optional.of(widget));
    when(repo.findById("p-2")).thenReturn(Optional.of(gadget));

    Order order =
        makeOrder(
            OrderStatus.CANCELLED,
            new OrderLineItem("p-1", "Widget", 3, new BigDecimal("10")),
            new OrderLineItem("p-2", "Gadget", 1, new BigDecimal("20")));

    new InventoryReleaseObserver(inv, repo).update(order);

    verify(inv, times(1)).addStock(eq(widget), eq(3));
    verify(inv, times(1)).addStock(eq(gadget), eq(1));
  }

  @Test
  void nonCancelledOrderIsIgnored() {
    IInventoryService inv = mock(IInventoryService.class);
    ProductRepository repo = mock(ProductRepository.class);

    Order order = makeOrder(OrderStatus.SHIPPED, new OrderLineItem("p-1", "Widget", 3, new BigDecimal("10")));

    new InventoryReleaseObserver(inv, repo).update(order);

    verify(inv, never()).addStock(any(BaseProduct.class), org.mockito.ArgumentMatchers.anyInt());
  }

  @Test
  void missingProductIsSkipped() {
    IInventoryService inv = mock(IInventoryService.class);
    ProductRepository repo = mock(ProductRepository.class);
    when(repo.findById("ghost")).thenReturn(Optional.empty());

    Order order =
        makeOrder(OrderStatus.CANCELLED, new OrderLineItem("ghost", "Ghost", 5, new BigDecimal("1")));

    new InventoryReleaseObserver(inv, repo).update(order);

    verify(inv, never()).addStock(any(BaseProduct.class), org.mockito.ArgumentMatchers.anyInt());
  }
}

package com.online.shop.core.observer;

import com.online.shop.model.Order;
import com.online.shop.model.OrderLineItem;
import com.online.shop.model.OrderStatus;
import com.online.shop.model.Product;
import com.online.shop.repository.ProductRepository;
import com.online.shop.service.inventory.IInventoryService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InventoryReleaseObserver implements OrderObserver {

  private static final Logger log = LoggerFactory.getLogger(InventoryReleaseObserver.class);

  private final IInventoryService inventoryService;
  private final ProductRepository productRepository;

  public InventoryReleaseObserver(
      IInventoryService inventoryService, ProductRepository productRepository) {
    this.inventoryService = inventoryService;
    this.productRepository = productRepository;
  }

  @Override
  public void update(Order order) {
    if (order.getStatus() != OrderStatus.CANCELLED) {
      return;
    }
    for (OrderLineItem item : order.getItems()) {
      Optional<Product> product = productRepository.findById(item.getProductId());
      if (product.isEmpty()) {
        log.warn(
            "Skipping stock release for order {}: product {} not found",
            order.getId(),
            item.getProductId());
        continue;
      }
      inventoryService.addStock(product.get(), item.getQuantity());
    }
  }
}

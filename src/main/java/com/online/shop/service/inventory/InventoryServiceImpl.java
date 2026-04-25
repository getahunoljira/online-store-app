package com.online.shop.service.inventory;

import com.online.shop.service.product.BaseProduct;
import com.online.shop.model.OrderLineItem;
import com.online.shop.model.Product;
import com.online.shop.repository.ProductRepository;
import com.online.shop.service.inventory.IInventoryService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements IInventoryService {

  private final ProductRepository productRepository;
  private final Map<String, Integer> fallbackStock = new HashMap<>();

  public InventoryServiceImpl(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public void addStock(BaseProduct product, int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    Optional<Product> persisted = productRepository.findById(product.getId());
    if (persisted.isPresent()) {
      Product p = persisted.get();
      p.setStockQuantity(p.getStockQuantity() + quantity);
      productRepository.save(p);
    } else {
      fallbackStock.merge(product.getId(), quantity, Integer::sum);
    }
  }

  @Override
  public int getStock(String productId) {
    return productRepository
        .findById(productId)
        .map(Product::getStockQuantity)
        .orElseGet(() -> fallbackStock.getOrDefault(productId, 0));
  }

  @Override
  public boolean isAvailable(String productId, int quantity) {
    return getStock(productId) >= quantity;
  }

  @Override
  public void updateStockForOrderList(List<OrderLineItem> lineItems) {
    for (OrderLineItem item : lineItems) {
      int current = getStock(item.getProductId());
      if (current < item.getQuantity()) {
        throw new IllegalArgumentException(
            "Insufficient stock for product "
                + item.getProductName()
                + ": available "
                + current
                + ", requested "
                + item.getQuantity());
      }
      Optional<Product> persisted = productRepository.findById(item.getProductId());
      if (persisted.isPresent()) {
        Product p = persisted.get();
        p.setStockQuantity(current - item.getQuantity());
        productRepository.save(p);
      } else {
        fallbackStock.put(item.getProductId(), current - item.getQuantity());
      }
    }
  }
}

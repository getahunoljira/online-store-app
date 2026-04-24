package com.online.shop.service;

import com.online.shop.domain.patterns.BaseProduct;
import com.online.shop.model.OrderLineItem;
import java.util.List;

public interface IInventoryService {

  void addStock(BaseProduct product, int quantity);

  int getStock(String productId);

  boolean isAvailable(String productId, int quantity);

  void updateStockForOrderList(List<OrderLineItem> lineItems);
}

package com.online.shop.service.inventory;

import com.online.shop.service.product.BaseProduct;
import com.online.shop.model.OrderLineItem;
import java.util.List;

public interface IInventoryService {

  void addStock(BaseProduct product, int quantity);

  int getStock(String productId);

  boolean isAvailable(String productId, int quantity);

  void updateStockForOrderList(List<OrderLineItem> lineItems);
}

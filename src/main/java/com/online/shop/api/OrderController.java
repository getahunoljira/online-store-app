package com.online.shop.api;

import com.online.shop.model.dto.OrderResponse;
import com.online.shop.model.dto.OrderStatusUpdateRequest;
import com.online.shop.exception.OrderNotFoundException;
import com.online.shop.model.Order;
import com.online.shop.service.order.IOrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final IOrderService orderService;

  public OrderController(IOrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping("/")
  public List<OrderResponse> listOrders(
      @RequestParam(name = "customer_id", required = false) String customerId) {
    List<Order> orders =
        customerId != null
            ? orderService.getCustomerOrders(customerId)
            : orderService.getAllOrders();
    return orders.stream().map(OrderResponse::from).toList();
  }

  @GetMapping("/{orderId}")
  public OrderResponse getOrder(@PathVariable String orderId) {
    Order order = orderService.getOrder(orderId);
    if (order == null) {
      throw new OrderNotFoundException(orderId);
    }
    return OrderResponse.from(order);
  }

  @PatchMapping("/{orderId}/status")
  public OrderResponse updateStatus(
      @PathVariable String orderId, @Valid @RequestBody OrderStatusUpdateRequest payload) {
    Order order =
        switch (payload.getAction()) {
          case "ship" -> orderService.shipOrder(orderId);
          case "deliver" -> orderService.deliverOrder(orderId);
          case "cancel" -> orderService.cancelOrder(orderId);
          default -> throw new IllegalArgumentException("Invalid action");
        };
    return OrderResponse.from(order);
  }
}

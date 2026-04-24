package com.online.shop.config;

import com.online.shop.domain.patterns.CreditCardPaymentStrategy;
import com.online.shop.domain.patterns.ShoppingCart;
import com.online.shop.model.Address;
import com.online.shop.model.Customer;
import com.online.shop.model.Order;
import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import com.online.shop.repository.ProductRepository;
import com.online.shop.service.ICartService;
import com.online.shop.service.ICustomerService;
import com.online.shop.service.IOrderService;
import com.online.shop.service.IPaymentService;
import com.online.shop.service.IProductService;
import com.online.shop.service.PaymentResult;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = true)
public class DevDataSeeder implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

  private final IProductService productService;
  private final ICustomerService customerService;
  private final ICartService cartService;
  private final IOrderService orderService;
  private final IPaymentService paymentService;
  private final ProductRepository productRepository;

  public DevDataSeeder(
      IProductService productService,
      ICustomerService customerService,
      ICartService cartService,
      IOrderService orderService,
      IPaymentService paymentService,
      ProductRepository productRepository) {
    this.productService = productService;
    this.customerService = customerService;
    this.cartService = cartService;
    this.orderService = orderService;
    this.paymentService = paymentService;
    this.productRepository = productRepository;
  }

  @Override
  public void run(String... args) {
    if (productRepository.count() > 0) {
      log.info("DevDataSeeder: data already present, skipping");
      return;
    }
    log.info("DevDataSeeder: seeding demo data");

    Product shirt = productService.create(
        "Cotton T-Shirt", new BigDecimal("19.99"), "100% cotton crew neck",
        ProductCategory.CLOTHING, 25);
    Product laptop = productService.create(
        "Laptop Pro 14\"", new BigDecimal("1499.00"), "Fast laptop with 16GB RAM",
        ProductCategory.ELECTRONICS, 8);
    Product book = productService.create(
        "Effective Java, 3rd Ed", new BigDecimal("44.95"), "Bloch",
        ProductCategory.BOOKS, 50);
    Product toaster = productService.create(
        "4-Slice Toaster", new BigDecimal("59.00"), "Stainless steel",
        ProductCategory.HOME_GOODS, 15);
    Product bananas = productService.create(
        "Bananas (bunch)", new BigDecimal("2.49"), "~5 count, organic",
        ProductCategory.GROCERY, 200);

    Customer alice = customerService.register(
        "Alice Anderson", "alice@example.com", "alice", "password123",
        new Address("1 Main St", "Springfield", "IL", "62701"));
    Customer bob = customerService.register(
        "Bob Baker", "bob@example.com", "bob", "password123",
        new Address("221B Baker St", "London", "GL", "NW1"));
    customerService.register(
        "Carol Chen", "carol@example.com", "carol", "password123",
        new Address("742 Evergreen Terrace", "Portland", "OR", "97201"));

    cartService.addItem(alice.getId(), shirt.getId(), 2);
    cartService.addItem(alice.getId(), book.getId(), 1);

    cartService.addItem(bob.getId(), laptop.getId(), 1);
    cartService.addItem(bob.getId(), toaster.getId(), 1);
    Order bobOrder = checkoutAndComplete(bob);
    orderService.shipOrder(bobOrder.getId());

    log.info(
        "DevDataSeeder: 5 products, 3 customers, 1 active cart, 1 shipped order seeded");
  }

  private Order checkoutAndComplete(Customer customer) {
    ShoppingCart cart = cartService.getCart(customer.getId());
    BigDecimal total = cart.calculateTotal();
    String idempotencyKey = "seed-" + customer.getId() + "-" + UUID.randomUUID();

    PaymentResult paymentResult = paymentService.processPayment(
        new CreditCardPaymentStrategy("4242424242424242"),
        customer.getId(), total, idempotencyKey, "USD", "Seed order for " + customer.getName());

    Order order = orderService.createOrder(customer, cart);
    Map<String, Object> raw = new HashMap<>();
    raw.put("transaction_id", paymentResult.getTransactionId());
    raw.put("message", paymentResult.getMessage());
    paymentService.complete(
        paymentResult.getPayment().getId(),
        paymentResult.getTransactionId(),
        order.getId(),
        raw);
    cartService.clearCart(customer.getId());
    return order;
  }
}

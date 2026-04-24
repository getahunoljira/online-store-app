package com.online.shop.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.online.shop.domain.patterns.ShoppingCart;
import com.online.shop.model.Customer;
import com.online.shop.model.Order;
import com.online.shop.model.Product;
import com.online.shop.repository.OrderRepository;
import com.online.shop.repository.ProductRepository;
import com.online.shop.service.ICustomerService;
import com.online.shop.service.IOrderService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

  private static final String CUSTOMER =
      """
      {
        "name": "Carol",
        "email": "carol@example.com",
        "username": "carol",
        "password": "pass123",
        "shipping_address": {
          "street": "2 Ave",
          "city": "Townville",
          "state": "CA",
          "zip_code": "90000"
        }
      }
      """;

  private static final String PRODUCT =
      """
      {"name":"Gadget","price":30.0,"category":"ELECTRONICS","initial_stock":100}
      """;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private OrderRepository orderRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private IOrderService orderService;
  @Autowired private ICustomerService customerService;

  @BeforeEach
  void reset() throws Exception {
    orderRepository.deleteAll();
    MvcResult customers = mockMvc.perform(get("/customers/")).andReturn();
    for (JsonNode c : objectMapper.readTree(customers.getResponse().getContentAsString())) {
      mockMvc.perform(delete("/customers/" + c.get("id").asText() + "/cart/"));
      mockMvc.perform(delete("/customers/" + c.get("id").asText()));
    }
    MvcResult products = mockMvc.perform(get("/products/")).andReturn();
    for (JsonNode p : objectMapper.readTree(products.getResponse().getContentAsString())) {
      mockMvc.perform(delete("/products/" + p.get("id").asText()));
    }
  }

  private record OrderAndCustomer(String orderId, String customerId) {}

  private OrderAndCustomer placeOrder() throws Exception {
    MvcResult cRes =
        mockMvc.perform(post("/customers/").contentType(MediaType.APPLICATION_JSON).content(CUSTOMER))
            .andReturn();
    String cid = objectMapper.readTree(cRes.getResponse().getContentAsString()).get("id").asText();

    MvcResult pRes =
        mockMvc.perform(post("/products/").contentType(MediaType.APPLICATION_JSON).content(PRODUCT))
            .andReturn();
    String pid = objectMapper.readTree(pRes.getResponse().getContentAsString()).get("id").asText();

    Customer customer = customerService.requireById(cid);
    Product product = productRepository.findById(pid).orElseThrow();
    ShoppingCart cart = new ShoppingCart();
    cart.addItem(product, 1);
    Order order = orderService.createOrder(customer, cart);
    return new OrderAndCustomer(order.getId(), cid);
  }

  @Test
  void getOrder() throws Exception {
    OrderAndCustomer placed = placeOrder();
    mockMvc
        .perform(get("/orders/" + placed.orderId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(placed.orderId()));
  }

  @Test
  void orderNotFound() throws Exception {
    mockMvc.perform(get("/orders/no-such")).andExpect(status().isNotFound());
  }

  @Test
  void listOrdersByCustomer() throws Exception {
    OrderAndCustomer placed = placeOrder();
    mockMvc
        .perform(get("/orders/").param("customer_id", placed.customerId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(1))));
  }

  @Test
  void shipOrder() throws Exception {
    OrderAndCustomer placed = placeOrder();
    mockMvc
        .perform(
            patch("/orders/" + placed.orderId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"ship\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("SHIPPED"));
  }

  @Test
  void deliverOrder() throws Exception {
    OrderAndCustomer placed = placeOrder();
    mockMvc.perform(
        patch("/orders/" + placed.orderId() + "/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"action\":\"ship\"}"));
    mockMvc
        .perform(
            patch("/orders/" + placed.orderId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"deliver\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DELIVERED"));
  }

  @Test
  void cancelOrder() throws Exception {
    OrderAndCustomer placed = placeOrder();
    mockMvc
        .perform(
            patch("/orders/" + placed.orderId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"cancel\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
  }

  @Test
  void invalidTransition() throws Exception {
    OrderAndCustomer placed = placeOrder();
    mockMvc
        .perform(
            patch("/orders/" + placed.orderId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"deliver\"}"))
        .andExpect(status().isBadRequest());
  }
}

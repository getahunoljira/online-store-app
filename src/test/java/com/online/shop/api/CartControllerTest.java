package com.online.shop.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
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
class CartControllerTest {

  private static final String CUSTOMER =
      """
      {
        "name": "Bob",
        "email": "bob@example.com",
        "username": "bob",
        "password": "pass123",
        "shipping_address": {
          "street": "1 St",
          "city": "City",
          "state": "ST",
          "zip_code": "00000"
        }
      }
      """;

  private static final String PRODUCT =
      """
      {"name":"Widget","price":20.0,"category":"ELECTRONICS","initial_stock":50}
      """;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void reset() throws Exception {
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

  private String[] setupCustomerAndProduct() throws Exception {
    MvcResult cRes =
        mockMvc
            .perform(
                post("/customers/").contentType(MediaType.APPLICATION_JSON).content(CUSTOMER))
            .andReturn();
    String cid =
        objectMapper.readTree(cRes.getResponse().getContentAsString()).get("id").asText();

    MvcResult pRes =
        mockMvc
            .perform(post("/products/").contentType(MediaType.APPLICATION_JSON).content(PRODUCT))
            .andReturn();
    String pid =
        objectMapper.readTree(pRes.getResponse().getContentAsString()).get("id").asText();
    return new String[] {cid, pid};
  }

  @Test
  void getEmptyCart() throws Exception {
    String cid = setupCustomerAndProduct()[0];
    mockMvc
        .perform(get("/customers/" + cid + "/cart/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", Matchers.hasSize(0)))
        .andExpect(jsonPath("$.total").value(0));
  }

  @Test
  void addItemToCart() throws Exception {
    String[] ids = setupCustomerAndProduct();
    String cid = ids[0];
    String pid = ids[1];
    String payload = String.format("{\"product_id\":\"%s\",\"quantity\":2}", pid);
    mockMvc
        .perform(
            post("/customers/" + cid + "/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isNoContent());
    mockMvc
        .perform(get("/customers/" + cid + "/cart/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", Matchers.hasSize(1)))
        .andExpect(jsonPath("$.items[0].quantity").value(2))
        .andExpect(jsonPath("$.total").value(40.0));
  }

  @Test
  void addItemUnknownProduct() throws Exception {
    String cid = setupCustomerAndProduct()[0];
    mockMvc
        .perform(
            post("/customers/" + cid + "/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"product_id\":\"no-such-id\",\"quantity\":1}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void addItemUnknownCustomer() throws Exception {
    mockMvc
        .perform(
            post("/customers/no-such-customer/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"product_id\":\"anything\",\"quantity\":1}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void removeItemFromCart() throws Exception {
    String[] ids = setupCustomerAndProduct();
    String cid = ids[0];
    String pid = ids[1];
    String addPayload = String.format("{\"product_id\":\"%s\",\"quantity\":1}", pid);
    mockMvc.perform(
        post("/customers/" + cid + "/cart/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(addPayload));
    mockMvc
        .perform(delete("/customers/" + cid + "/cart/items/" + pid))
        .andExpect(status().isNoContent());
    mockMvc
        .perform(get("/customers/" + cid + "/cart/"))
        .andExpect(jsonPath("$.items", Matchers.hasSize(0)));
  }

  @Test
  void clearCart() throws Exception {
    String[] ids = setupCustomerAndProduct();
    String cid = ids[0];
    String pid = ids[1];
    String addPayload = String.format("{\"product_id\":\"%s\",\"quantity\":3}", pid);
    mockMvc.perform(
        post("/customers/" + cid + "/cart/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(addPayload));
    mockMvc
        .perform(delete("/customers/" + cid + "/cart/"))
        .andExpect(status().isNoContent());
    mockMvc
        .perform(get("/customers/" + cid + "/cart/"))
        .andExpect(jsonPath("$.items", Matchers.hasSize(0)));
  }

  @Test
  void addSameItemTwiceIncrementsQuantity() throws Exception {
    String[] ids = setupCustomerAndProduct();
    String cid = ids[0];
    String pid = ids[1];
    String payload = String.format("{\"product_id\":\"%s\",\"quantity\":2}", pid);
    mockMvc.perform(
        post("/customers/" + cid + "/cart/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload));
    mockMvc.perform(
        post("/customers/" + cid + "/cart/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload));
    mockMvc
        .perform(get("/customers/" + cid + "/cart/"))
        .andExpect(jsonPath("$.items", Matchers.hasSize(1)))
        .andExpect(jsonPath("$.items[0].quantity").value(4));
  }
}

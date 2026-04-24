package com.online.shop.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class CustomerControllerTest {

  private static final String CUSTOMER_PAYLOAD =
      """
      {
        "name": "Alice Smith",
        "email": "alice@example.com",
        "username": "alice",
        "password": "secret123",
        "shipping_address": {
          "street": "123 Main St",
          "city": "Springfield",
          "state": "IL",
          "zip_code": "62701"
        }
      }
      """;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void reset() throws Exception {
    MvcResult list = mockMvc.perform(get("/customers/")).andReturn();
    JsonNode items = objectMapper.readTree(list.getResponse().getContentAsString());
    for (JsonNode item : items) {
      mockMvc.perform(delete("/customers/" + item.get("id").asText()));
    }
  }

  private String registerCustomer() throws Exception {
    MvcResult res =
        mockMvc
            .perform(
                post("/customers/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(CUSTOMER_PAYLOAD))
            .andReturn();
    return objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();
  }

  @Test
  void registerNewCustomer() throws Exception {
    mockMvc
        .perform(
            post("/customers/").contentType(MediaType.APPLICATION_JSON).content(CUSTOMER_PAYLOAD))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Alice Smith"))
        .andExpect(jsonPath("$.email").value("alice@example.com"))
        .andExpect(jsonPath("$.shipping_address.city").value("Springfield"));
  }

  @Test
  void registerDuplicateEmail() throws Exception {
    mockMvc.perform(
        post("/customers/").contentType(MediaType.APPLICATION_JSON).content(CUSTOMER_PAYLOAD));
    mockMvc
        .perform(
            post("/customers/").contentType(MediaType.APPLICATION_JSON).content(CUSTOMER_PAYLOAD))
        .andExpect(status().isConflict());
  }

  @Test
  void getCustomer() throws Exception {
    String id = registerCustomer();
    mockMvc
        .perform(get("/customers/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id));
  }

  @Test
  void getCustomerNotFound() throws Exception {
    mockMvc.perform(get("/customers/no-such-id")).andExpect(status().isNotFound());
  }

  @Test
  void listCustomers() throws Exception {
    registerCustomer();
    mockMvc
        .perform(get("/customers/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(1))));
  }

  @Test
  void updateShippingAddress() throws Exception {
    String id = registerCustomer();
    String newAddr =
        """
        {
          "shipping_address": {
            "street": "999 Oak Ave",
            "city": "Shelbyville",
            "state": "IL",
            "zip_code": "62565"
          }
        }
        """;
    mockMvc
        .perform(
            put("/customers/" + id + "/shipping-address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newAddr))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.shipping_address.city").value("Shelbyville"));
  }

  @Test
  void deleteCustomer() throws Exception {
    String id = registerCustomer();
    mockMvc.perform(delete("/customers/" + id)).andExpect(status().isNoContent());
    mockMvc.perform(get("/customers/" + id)).andExpect(status().isNotFound());
  }
}

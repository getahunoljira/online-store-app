package com.online.shop.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
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
class ProductControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void reset() throws Exception {
    MvcResult list = mockMvc.perform(get("/products/")).andReturn();
    JsonNode items = objectMapper.readTree(list.getResponse().getContentAsString());
    for (JsonNode item : items) {
      mockMvc.perform(delete("/products/" + item.get("id").asText()));
    }
  }

  private String createProduct(String body) throws Exception {
    MvcResult res =
        mockMvc
            .perform(post("/products/").contentType(MediaType.APPLICATION_JSON).content(body))
            .andReturn();
    return objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();
  }

  @Test
  void createProduct() throws Exception {
    mockMvc
        .perform(
            post("/products/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"name":"Laptop","price":999.99,"description":"Powerful laptop","category":"ELECTRONICS","initial_stock":10}
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Laptop"))
        .andExpect(jsonPath("$.price").value(999.99))
        .andExpect(jsonPath("$.category").value("ELECTRONICS"))
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  void getProduct() throws Exception {
    String id =
        createProduct(
            """
            {"name":"Book","price":15.0,"category":"BOOKS","initial_stock":5}
            """);
    mockMvc
        .perform(get("/products/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id));
  }

  @Test
  void getProductNotFound() throws Exception {
    mockMvc.perform(get("/products/nonexistent-id")).andExpect(status().isNotFound());
  }

  @Test
  void listProducts() throws Exception {
    createProduct("""
        {"name":"A","price":1.0,"category":"BOOKS"}
        """);
    createProduct("""
        {"name":"B","price":2.0,"category":"ELECTRONICS"}
        """);
    mockMvc
        .perform(get("/products/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void listProductsByCategory() throws Exception {
    createProduct("""
        {"name":"A","price":1.0,"category":"BOOKS"}
        """);
    createProduct("""
        {"name":"B","price":2.0,"category":"ELECTRONICS"}
        """);
    mockMvc
        .perform(get("/products/?category=BOOKS"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].category").value("BOOKS"));
  }

  @Test
  void listProductsByName() throws Exception {
    createProduct(
        """
        {"name":"Python Book","price":29.99,"category":"BOOKS"}
        """);
    createProduct(
        """
        {"name":"Java Book","price":25.0,"category":"BOOKS"}
        """);
    mockMvc
        .perform(get("/products/?name=Python"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", containsString("Python")));
  }

  @Test
  void deleteProduct() throws Exception {
    String id = createProduct("""
        {"name":"X","price":1.0,"category":"BOOKS"}
        """);
    mockMvc.perform(delete("/products/" + id)).andExpect(status().isNoContent());
    mockMvc.perform(get("/products/" + id)).andExpect(status().isNotFound());
  }

  @Test
  void getStock() throws Exception {
    String id =
        createProduct(
            """
            {"name":"Widget","price":5.0,"category":"HOME_GOODS","initial_stock":20}
            """);
    mockMvc
        .perform(get("/products/" + id + "/stock"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stock", equalTo(20)));
  }

  @Test
  void addStock() throws Exception {
    String id =
        createProduct(
            """
            {"name":"Widget","price":5.0,"category":"HOME_GOODS","initial_stock":5}
            """);
    mockMvc
        .perform(
            post("/products/" + id + "/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":10}"))
        .andExpect(status().isNoContent());
    mockMvc
        .perform(get("/products/" + id + "/stock"))
        .andExpect(jsonPath("$.stock", equalTo(15)));
  }
}

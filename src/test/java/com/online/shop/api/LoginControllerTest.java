package com.online.shop.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.online.shop.model.Account;
import com.online.shop.repository.AccountRepository;
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
class LoginControllerTest {

  private static final String CUSTOMER =
      """
      {
        "name": "Alice Smith",
        "email": "alice@example.com",
        "username": "alice",
        "password": "secret123"
      }
      """;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private AccountRepository accountRepository;

  @BeforeEach
  void reset() throws Exception {
    MvcResult list = mockMvc.perform(get("/customers/")).andReturn();
    JsonNode items = objectMapper.readTree(list.getResponse().getContentAsString());
    for (JsonNode item : items) {
      mockMvc.perform(delete("/customers/" + item.get("id").asText()));
    }
  }

  private void register() throws Exception {
    mockMvc.perform(
        post("/customers/").contentType(MediaType.APPLICATION_JSON).content(CUSTOMER));
  }

  @Test
  void loginSuccess() throws Exception {
    register();
    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"secret123\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("alice@example.com"))
        .andExpect(jsonPath("$.name").value("Alice Smith"))
        .andExpect(jsonPath("$.customer_id").exists())
        .andExpect(jsonPath("$.message").value("Login successful"));
  }

  @Test
  void loginWrongPassword() throws Exception {
    register();
    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"wrongpass\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void loginUnknownUsername() throws Exception {
    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"nobody\",\"password\":\"pass\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void loginMissingFields() throws Exception {
    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\"}"))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void passwordIsHashedInDb() throws Exception {
    register();
    Account account = accountRepository.findByUsername("alice").orElseThrow();
    org.assertj.core.api.Assertions.assertThat(account.getPasswordHash()).isNotEqualTo("secret123");
    org.assertj.core.api.Assertions.assertThat(account.getPasswordHash()).startsWith("$2");
  }
}

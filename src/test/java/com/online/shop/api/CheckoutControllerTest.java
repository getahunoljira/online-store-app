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
class CheckoutControllerTest {

  private static final String CUSTOMER =
      """
      {
        "name": "Dave",
        "email": "dave@example.com",
        "username": "dave",
        "password": "pass123",
        "shipping_address": {
          "street": "5 Oak Lane",
          "city": "Riverside",
          "state": "CA",
          "zip_code": "92501"
        }
      }
      """;

  private static final String PRODUCT =
      """
      {"name":"Headphones","price":50.0,"category":"ELECTRONICS","initial_stock":20}
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

  private String[] setupCustomerProductCart(String customerJson) throws Exception {
    MvcResult cRes =
        mockMvc
            .perform(
                post("/customers/").contentType(MediaType.APPLICATION_JSON).content(customerJson))
            .andReturn();
    String cid = objectMapper.readTree(cRes.getResponse().getContentAsString()).get("id").asText();

    MvcResult pRes =
        mockMvc
            .perform(post("/products/").contentType(MediaType.APPLICATION_JSON).content(PRODUCT))
            .andReturn();
    String pid = objectMapper.readTree(pRes.getResponse().getContentAsString()).get("id").asText();

    mockMvc.perform(
        post("/customers/" + cid + "/cart/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"product_id\":\"%s\",\"quantity\":2}", pid)));
    return new String[] {cid, pid};
  }

  private String[] setup() throws Exception {
    return setupCustomerProductCart(CUSTOMER);
  }

  @Test
  void checkoutCreditCardSuccess() throws Exception {
    String cid = setup()[0];
    String payload =
        """
        {
          "payment_method": "credit_card",
          "card_number": "4111111111111111",
          "card_expiry_month": 12,
          "card_expiry_year": 2026,
          "card_cvv": "123",
          "card_holder_name": "Dave Test"
        }
        """;
    mockMvc
        .perform(
            post("/customers/" + cid + "/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.order_status").value("PLACED"))
        .andExpect(jsonPath("$.total_amount").value(100.0))
        .andExpect(jsonPath("$.payment.status").value("COMPLETED"))
        .andExpect(jsonPath("$.payment.method").value("credit_card"))
        .andExpect(jsonPath("$.payment.transaction_id", Matchers.startsWith("mock_txn_")))
        .andExpect(jsonPath("$.items", Matchers.hasSize(1)));
  }

  @Test
  void checkoutUpiSuccess() throws Exception {
    String cid = setup()[0];
    String payload =
        """
        {
          "payment_method": "upi",
          "upi_id": "dave@testbank"
        }
        """;
    mockMvc
        .perform(
            post("/customers/" + cid + "/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.payment.method").value("upi"))
        .andExpect(jsonPath("$.payment.status").value("COMPLETED"));
  }

  @Test
  void checkoutClearsCart() throws Exception {
    String cid = setup()[0];
    mockMvc.perform(
        post("/customers/" + cid + "/cart/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"payment_method\":\"credit_card\",\"card_number\":\"4111111111111111\"}"));
    mockMvc
        .perform(get("/customers/" + cid + "/cart/"))
        .andExpect(jsonPath("$.items", Matchers.hasSize(0)));
  }

  @Test
  void checkoutReducesStock() throws Exception {
    String[] ids = setup();
    String cid = ids[0];
    String pid = ids[1];
    MvcResult before = mockMvc.perform(get("/products/" + pid + "/stock")).andReturn();
    int stockBefore =
        objectMapper.readTree(before.getResponse().getContentAsString()).get("stock").asInt();
    mockMvc.perform(
        post("/customers/" + cid + "/cart/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"payment_method\":\"credit_card\",\"card_number\":\"4111111111111111\"}"));
    MvcResult after = mockMvc.perform(get("/products/" + pid + "/stock")).andReturn();
    int stockAfter =
        objectMapper.readTree(after.getResponse().getContentAsString()).get("stock").asInt();
    org.assertj.core.api.Assertions.assertThat(stockAfter).isEqualTo(stockBefore - 2);
  }

  @Test
  void checkoutDeclinedCardReturns402() throws Exception {
    String cid = setup()[0];
    mockMvc
        .perform(
            post("/customers/" + cid + "/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"payment_method\":\"credit_card\",\"card_number\":\"4000000000000002\"}"))
        .andExpect(status().isPaymentRequired())
        .andExpect(jsonPath("$.detail.error").value("Payment failed"))
        .andExpect(jsonPath("$.detail.payment_id").exists());
  }

  @Test
  void checkoutInvalidUpiReturns402() throws Exception {
    String cid = setup()[0];
    mockMvc
        .perform(
            post("/customers/" + cid + "/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"payment_method\":\"upi\",\"upi_id\":\"invalid-no-at-sign\"}"))
        .andExpect(status().isPaymentRequired());
  }

  @Test
  void checkoutFailedPaymentDoesNotCreateOrder() throws Exception {
    String cid = setup()[0];
    mockMvc.perform(
        post("/customers/" + cid + "/cart/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"payment_method\":\"credit_card\",\"card_number\":\"4000000000000002\"}"));
    mockMvc
        .perform(get("/orders/").param("customer_id", cid))
        .andExpect(jsonPath("$", Matchers.hasSize(0)));
  }

  @Test
  void checkoutFailedPaymentCartRemains() throws Exception {
    String cid = setup()[0];
    mockMvc.perform(
        post("/customers/" + cid + "/cart/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"payment_method\":\"credit_card\",\"card_number\":\"4000000000000002\"}"));
    mockMvc
        .perform(get("/customers/" + cid + "/cart/"))
        .andExpect(jsonPath("$.items", Matchers.hasSize(1)));
  }

  @Test
  void checkoutMissingCardNumber() throws Exception {
    String cid = setup()[0];
    mockMvc
        .perform(
            post("/customers/" + cid + "/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"payment_method\":\"credit_card\"}"))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void checkoutMissingUpiId() throws Exception {
    String cid = setup()[0];
    mockMvc
        .perform(
            post("/customers/" + cid + "/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"payment_method\":\"upi\"}"))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void checkoutNoShippingAddress() throws Exception {
    String noAddr =
        """
        {"name":"No Addr","email":"noaddr@example.com","username":"noaddr","password":"pass123"}
        """;
    String cid = setupCustomerProductCart(noAddr)[0];
    mockMvc
        .perform(
            post("/customers/" + cid + "/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"payment_method\":\"credit_card\",\"card_number\":\"4111111111111111\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void paymentHistoryAfterCheckout() throws Exception {
    String cid = setup()[0];
    mockMvc.perform(
        post("/customers/" + cid + "/cart/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"payment_method\":\"credit_card\",\"card_number\":\"4111111111111111\"}"));
    mockMvc
        .perform(get("/customers/" + cid + "/cart/payments"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", Matchers.hasSize(1)))
        .andExpect(jsonPath("$[0].status").value("COMPLETED"))
        .andExpect(jsonPath("$[0].method").value("credit_card"));
  }

  @Test
  void paymentHistoryIncludesFailedAttempts() throws Exception {
    String cid = setup()[0];
    mockMvc.perform(
        post("/customers/" + cid + "/cart/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"payment_method\":\"credit_card\",\"card_number\":\"4000000000000002\"}"));
    MvcResult res =
        mockMvc
            .perform(get("/customers/" + cid + "/cart/payments"))
            .andExpect(status().isOk())
            .andReturn();
    JsonNode payments = objectMapper.readTree(res.getResponse().getContentAsString());
    boolean hasFailed = false;
    for (JsonNode p : payments) {
      if ("FAILED".equals(p.get("status").asText())) {
        hasFailed = true;
        break;
      }
    }
    org.assertj.core.api.Assertions.assertThat(hasFailed).isTrue();
  }

  @Test
  void refundCompletedPayment() throws Exception {
    String cid = setup()[0];
    MvcResult checkout =
        mockMvc
            .perform(
                post("/customers/" + cid + "/cart/checkout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"payment_method\":\"credit_card\",\"card_number\":\"4111111111111111\"}"))
            .andReturn();
    String paymentId =
        objectMapper
            .readTree(checkout.getResponse().getContentAsString())
            .get("payment")
            .get("payment_id")
            .asText();
    mockMvc
        .perform(post("/customers/" + cid + "/cart/payments/" + paymentId + "/refund"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REFUNDED"))
        .andExpect(jsonPath("$.refund_transaction_id", Matchers.notNullValue()));
  }

  @Test
  void refundNonexistentPayment() throws Exception {
    String cid = setup()[0];
    mockMvc
        .perform(post("/customers/" + cid + "/cart/payments/no-such-id/refund"))
        .andExpect(status().isNotFound());
  }
}

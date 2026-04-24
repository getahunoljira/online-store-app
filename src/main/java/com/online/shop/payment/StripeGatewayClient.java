package com.online.shop.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import tools.jackson.databind.ObjectMapper;

public class StripeGatewayClient implements PaymentGatewayClient {

  private final String apiKey;
  private final String baseUrl;
  private final Duration timeout;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public StripeGatewayClient(String apiKey) {
    this(apiKey, "https://api.stripe.com/v1", Duration.ofSeconds(10));
  }

  public StripeGatewayClient(String apiKey, String baseUrl, Duration timeout) {
    this.apiKey = apiKey;
    this.baseUrl = baseUrl;
    this.timeout = timeout;
    this.httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
  }

  public StripeGatewayClient(
      String apiKey, String baseUrl, Duration timeout, HttpClient httpClient) {
    this.apiKey = apiKey;
    this.baseUrl = baseUrl;
    this.timeout = timeout;
    this.httpClient = httpClient;
  }

  @Override
  public GatewayChargeResult charge(GatewayChargeRequest request) {
    long amountCents = request.getAmount().multiply(BigDecimal.valueOf(100))
        .setScale(0, RoundingMode.HALF_UP).longValueExact();

    Map<String, String> form = new LinkedHashMap<>();
    form.put("amount", Long.toString(amountCents));
    form.put("currency", request.getCurrency().toLowerCase());
    form.put("description",
        request.getDescription() == null || request.getDescription().isEmpty()
            ? "Online shopping purchase"
            : request.getDescription());

    if ("credit_card".equals(request.getMethodType()) && request.getCardNumber() != null) {
      form.put("source[object]", "card");
      form.put("source[number]", request.getCardNumber());
      if (request.getCardExpiryMonth() != null) {
        form.put("source[exp_month]", request.getCardExpiryMonth().toString());
      }
      if (request.getCardExpiryYear() != null) {
        form.put("source[exp_year]", request.getCardExpiryYear().toString());
      }
      if (request.getCardCvv() != null) {
        form.put("source[cvc]", request.getCardCvv());
      }
      if (request.getCardHolderName() != null) {
        form.put("source[name]", request.getCardHolderName());
      }
    } else if ("upi".equals(request.getMethodType()) && request.getUpiId() != null) {
      form.put("payment_method_data[type]", "upi");
      form.put("payment_method_data[upi][vpa]", request.getUpiId());
    }

    try {
      HttpRequest httpRequest =
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/charges"))
              .timeout(timeout)
              .header("Content-Type", "application/x-www-form-urlencoded")
              .header(
                  "Authorization",
                  "Basic "
                      + Base64.getEncoder()
                          .encodeToString((apiKey + ":").getBytes(StandardCharsets.UTF_8)))
              .header("Idempotency-Key", request.getIdempotencyKey())
              .POST(HttpRequest.BodyPublishers.ofString(encodeForm(form)))
              .build();

      HttpResponse<String> resp = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
      Map<String, Object> body = parseJson(resp.body());

      if (resp.statusCode() == 200 || resp.statusCode() == 201) {
        boolean paid = Boolean.TRUE.equals(body.get("paid"));
        Number amount = (Number) body.getOrDefault("amount", 0);
        String currency = String.valueOf(body.getOrDefault("currency", request.getCurrency()));
        String id = String.valueOf(body.getOrDefault("id", ""));
        String failureMessage = (String) body.get("failure_message");
        GatewayChargeResult result =
            new GatewayChargeResult(
                id,
                paid ? "succeeded" : "failed",
                BigDecimal.valueOf(amount.longValue()).divide(BigDecimal.valueOf(100)),
                currency.toUpperCase(),
                failureMessage == null ? "Payment processed" : failureMessage);
        result.setRawResponse(body);
        return result;
      }

      @SuppressWarnings("unchecked")
      Map<String, Object> error =
          (Map<String, Object>) body.getOrDefault("error", new HashMap<>());
      GatewayChargeResult result =
          new GatewayChargeResult(
              "",
              "failed",
              BigDecimal.ZERO,
              request.getCurrency(),
              String.valueOf(error.getOrDefault("message", "Payment declined")));
      result.setRawResponse(body);
      return result;

    } catch (HttpTimeoutException e) {
      return new GatewayChargeResult(
          "", "failed", BigDecimal.ZERO, request.getCurrency(), "Payment gateway timeout");
    } catch (Exception e) {
      return new GatewayChargeResult(
          "",
          "failed",
          BigDecimal.ZERO,
          request.getCurrency(),
          "Gateway connection error: " + e.getMessage());
    }
  }

  @Override
  public GatewayRefundResult refund(String transactionId, BigDecimal amount) {
    Map<String, String> form = new LinkedHashMap<>();
    form.put("charge", transactionId);
    if (amount != null) {
      long cents = amount.multiply(BigDecimal.valueOf(100))
          .setScale(0, RoundingMode.HALF_UP).longValueExact();
      form.put("amount", Long.toString(cents));
    }

    try {
      HttpRequest httpRequest =
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/refunds"))
              .timeout(timeout)
              .header("Content-Type", "application/x-www-form-urlencoded")
              .header(
                  "Authorization",
                  "Basic "
                      + Base64.getEncoder()
                          .encodeToString((apiKey + ":").getBytes(StandardCharsets.UTF_8)))
              .POST(HttpRequest.BodyPublishers.ofString(encodeForm(form)))
              .build();

      HttpResponse<String> resp = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
      Map<String, Object> body = parseJson(resp.body());

      if (resp.statusCode() == 200 || resp.statusCode() == 201) {
        Number refundAmount = (Number) body.getOrDefault("amount", 0);
        return new GatewayRefundResult(
            String.valueOf(body.getOrDefault("id", "")),
            "succeeded",
            BigDecimal.valueOf(refundAmount.longValue()).divide(BigDecimal.valueOf(100)),
            "Refund issued");
      }

      @SuppressWarnings("unchecked")
      Map<String, Object> error =
          (Map<String, Object>) body.getOrDefault("error", new HashMap<>());
      return new GatewayRefundResult(
          "",
          "failed",
          BigDecimal.ZERO,
          String.valueOf(error.getOrDefault("message", "Refund failed")));
    } catch (Exception e) {
      return new GatewayRefundResult(
          "", "failed", BigDecimal.ZERO, "Gateway connection error: " + e.getMessage());
    }
  }

  private static String encodeForm(Map<String, String> form) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> e : form.entrySet()) {
      if (sb.length() > 0) sb.append('&');
      sb.append(urlEncode(e.getKey())).append('=').append(urlEncode(e.getValue()));
    }
    return sb.toString();
  }

  private static String urlEncode(String s) {
    return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> parseJson(String body) {
    if (body == null || body.isEmpty()) {
      return new HashMap<>();
    }
    try {
      return objectMapper.readValue(body, Map.class);
    } catch (Exception e) {
      return new HashMap<>();
    }
  }
}

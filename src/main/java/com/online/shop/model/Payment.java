package com.online.shop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "payments",
    indexes = {
      @Index(name = "idx_payment_customer", columnList = "customer_id"),
      @Index(name = "idx_payment_order", columnList = "order_id"),
      @Index(name = "idx_payment_status", columnList = "status"),
      @Index(name = "idx_payment_idempotency", columnList = "idempotency_key", unique = true)
    })
public class Payment {

  @Id
  @Column(length = 36)
  private String id;

  @Column(name = "customer_id", nullable = false, length = 36)
  private String customerId;

  @Column(name = "order_id", length = 36)
  private String orderId;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false, length = 10)
  private String currency = "USD";

  @Column(nullable = false, length = 30)
  private String method;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status = PaymentStatus.PENDING;

  @Column(name = "transaction_id", length = 200)
  private String transactionId;

  @Column(name = "gateway_response", columnDefinition = "TEXT")
  private String gatewayResponse;

  @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
  private String idempotencyKey;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  protected Payment() {}

  public Payment(
      String customerId,
      BigDecimal amount,
      String currency,
      String method,
      String idempotencyKey) {
    this.id = UUID.randomUUID().toString();
    this.customerId = customerId;
    this.amount = amount;
    this.currency = currency == null ? "USD" : currency;
    this.method = method;
    this.idempotencyKey = idempotencyKey;
    this.status = PaymentStatus.PENDING;
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  public String getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public String getMethod() {
    return method;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public String getGatewayResponse() {
    return gatewayResponse;
  }

  public void setGatewayResponse(String gatewayResponse) {
    this.gatewayResponse = gatewayResponse;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void touch() {
    this.updatedAt = LocalDateTime.now();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Payment p)) return false;
    return Objects.equals(id, p.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

package com.online.shop.model;

import com.online.shop.service.product.BaseProduct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "products",
    indexes = {
      @Index(name = "idx_product_name", columnList = "name"),
      @Index(name = "idx_product_category", columnList = "category")
    })
public class Product implements BaseProduct {

  @Id
  @Column(length = 36)
  private String id;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(length = 1000)
  private String description = "";

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private ProductCategory category;

  @Column(name = "stock_quantity", nullable = false)
  private int stockQuantity = 0;

  protected Product() {
  }

  public Product(String name, BigDecimal price, String description, ProductCategory category) {
    this(null, name, price, description, category);
  }

  public Product(String id, String name, BigDecimal price, String description, ProductCategory category) {
    this.id = id != null ? id : UUID.randomUUID().toString();
    this.name = name;
    this.price = price;
    this.description = description == null ? "" : description;
    this.category = category;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public BigDecimal getPrice() {
    return price;
  }

  @Override
  public ProductCategory getCategory() {
    return category;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public int getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(int stockQuantity) {
    this.stockQuantity = stockQuantity;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public void setDescription(String description) {
    this.description = description == null ? "" : description;
  }

  public void setCategory(ProductCategory category) {
    this.category = category;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Product p)) return false;
    return Objects.equals(id, p.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Product(id=" + id + ", name=" + name + ", price=" + price + ", category=" + category + ")";
  }
}

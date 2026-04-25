package com.online.shop.api;

import com.online.shop.model.dto.ProductCreateRequest;
import com.online.shop.model.dto.ProductResponse;
import com.online.shop.model.dto.StockResponse;
import com.online.shop.model.dto.StockUpdateRequest;
import com.online.shop.exception.ProductNotFoundException;
import com.online.shop.model.Product;
import com.online.shop.model.ProductCategory;
import com.online.shop.service.inventory.IInventoryService;
import com.online.shop.service.product.IProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final IProductService productService;
  private final IInventoryService inventoryService;

  public ProductController(
      IProductService productService,
      IInventoryService inventoryService) {
    this.productService = productService;
    this.inventoryService = inventoryService;
  }

  @PostMapping("/")
  @ResponseStatus(HttpStatus.CREATED)
  public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest payload) {
    Product created =
        productService.create(
            payload.getName(),
            payload.getPrice(),
            payload.getDescription(),
            payload.getCategory(),
            payload.getInitialStock());
    return ProductResponse.from(created);
  }

  @GetMapping("/")
  public List<ProductResponse> listProducts(
      @RequestParam(value = "category", required = false) ProductCategory category,
      @RequestParam(value = "name", required = false) String name) {
    List<Product> products;
    if (category != null) {
      products = productService.searchByCategory(category);
    } else if (name != null) {
      products = productService.searchByName(name);
    } else {
      products = productService.getAllProducts();
    }
    return products.stream().map(ProductResponse::from).toList();
  }

  @GetMapping("/{productId}")
  public ProductResponse getProduct(@PathVariable String productId) {
    return ProductResponse.from(productService.requireById(productId));
  }

  @PostMapping("/{productId}/stock")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addStock(
      @PathVariable String productId, @Valid @RequestBody StockUpdateRequest payload) {
    Product product = productService.requireById(productId);
    inventoryService.addStock(product, payload.getQuantity());
  }

  @GetMapping("/{productId}/stock")
  public StockResponse getStock(@PathVariable String productId) {
    if (productService.getById(productId).isEmpty()) {
      throw new ProductNotFoundException(productId);
    }
    return new StockResponse(productId, inventoryService.getStock(productId));
  }

  @DeleteMapping("/{productId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProduct(@PathVariable String productId) {
    productService.delete(productId);
  }
}

package com.online.shop.api;

import com.online.shop.model.dto.CustomerCreateRequest;
import com.online.shop.model.dto.CustomerResponse;
import com.online.shop.model.dto.ShippingAddressUpdateRequest;
import com.online.shop.model.Address;
import com.online.shop.model.Customer;
import com.online.shop.service.customer.ICustomerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {

  private final ICustomerService customerService;

  public CustomerController(ICustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping("/")
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerResponse registerCustomer(@Valid @RequestBody CustomerCreateRequest payload) {
    Address shippingAddress =
        payload.getShippingAddress() == null ? null : payload.getShippingAddress().toEntity();
    Customer created =
        customerService.register(
            payload.getName(),
            payload.getEmail(),
            payload.getUsername(),
            payload.getPassword(),
            shippingAddress);
    return CustomerResponse.from(created);
  }

  @GetMapping("/")
  public List<CustomerResponse> listCustomers() {
    return customerService.getAll().stream().map(CustomerResponse::from).toList();
  }

  @GetMapping("/{customerId}")
  public CustomerResponse getCustomer(@PathVariable String customerId) {
    return CustomerResponse.from(customerService.requireById(customerId));
  }

  @PutMapping("/{customerId}/shipping-address")
  public CustomerResponse updateShippingAddress(
      @PathVariable String customerId, @Valid @RequestBody ShippingAddressUpdateRequest payload) {
    Customer updated =
        customerService.updateShippingAddress(customerId, payload.getShippingAddress().toEntity());
    return CustomerResponse.from(updated);
  }

  @DeleteMapping("/{customerId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteCustomer(@PathVariable String customerId) {
    customerService.delete(customerId);
  }
}

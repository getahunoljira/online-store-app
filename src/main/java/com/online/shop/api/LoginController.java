package com.online.shop.api;

import com.online.shop.model.dto.LoginRequest;
import com.online.shop.model.dto.LoginResponse;
import com.online.shop.model.Customer;
import com.online.shop.service.customer.ICustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

  private final ICustomerService customerService;

  public LoginController(ICustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest payload) {
    Customer customer = customerService.authenticate(payload.getUsername(), payload.getPassword());
    return new LoginResponse(customer.getId(), customer.getName(), customer.getEmail());
  }
}

package com.online.shop.service.customer;

import com.online.shop.model.Address;
import com.online.shop.model.Customer;
import java.util.List;
import java.util.Optional;

public interface ICustomerService {

  Customer register(
      String name, String email, String username, String password, Address shippingAddress);

  Optional<Customer> findById(String id);

  Customer requireById(String id);

  List<Customer> getAll();

  Customer updateShippingAddress(String customerId, Address newAddress);

  void delete(String id);

  Customer authenticate(String username, String password);
}

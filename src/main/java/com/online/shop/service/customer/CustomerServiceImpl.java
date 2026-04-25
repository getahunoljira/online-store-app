package com.online.shop.service.customer;

import com.online.shop.exception.CustomerNotFoundException;
import com.online.shop.exception.EmailAlreadyRegisteredException;
import com.online.shop.exception.InvalidCredentialsException;
import com.online.shop.model.Account;
import com.online.shop.model.Address;
import com.online.shop.model.Customer;
import com.online.shop.repository.AccountRepository;
import com.online.shop.repository.CustomerRepository;
import com.online.shop.service.customer.ICustomerService;
import com.online.shop.util.PasswordHasher;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements ICustomerService {

  private final CustomerRepository customerRepository;
  private final AccountRepository accountRepository;
  private final PasswordHasher passwordHasher;

  public CustomerServiceImpl(
      CustomerRepository customerRepository,
      AccountRepository accountRepository,
      PasswordHasher passwordHasher) {
    this.customerRepository = customerRepository;
    this.accountRepository = accountRepository;
    this.passwordHasher = passwordHasher;
  }

  @Override
  @Transactional
  public Customer register(
      String name, String email, String username, String password, Address shippingAddress) {
    if (customerRepository.findByEmail(email).isPresent()) {
      throw new EmailAlreadyRegisteredException(email);
    }
    Account account = new Account(username, passwordHasher.hash(password));
    Customer customer = new Customer(name, email, account, shippingAddress);
    return customerRepository.save(customer);
  }

  @Override
  public Optional<Customer> findById(String id) {
    return customerRepository.findById(id);
  }

  @Override
  public Customer requireById(String id) {
    return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
  }

  @Override
  public List<Customer> getAll() {
    return customerRepository.findAll();
  }

  @Override
  @Transactional
  public Customer updateShippingAddress(String customerId, Address newAddress) {
    Customer customer = requireById(customerId);
    Address existing = customer.getShippingAddress();
    if (existing != null) {
      existing.setStreet(newAddress.getStreet());
      existing.setCity(newAddress.getCity());
      existing.setState(newAddress.getState());
      existing.setZipCode(newAddress.getZipCode());
    } else {
      customer.setShippingAddress(newAddress);
    }
    return customerRepository.save(customer);
  }

  @Override
  @Transactional
  public void delete(String id) {
    Customer customer =
        customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    Account account = customer.getAccount();
    customerRepository.delete(customer);
    customerRepository.flush();
    if (account != null) {
      accountRepository.deleteById(account.getId());
    }
  }

  @Override
  public Customer authenticate(String username, String password) {
    Customer customer =
        customerRepository
            .findByAccountUsername(username)
            .orElseThrow(InvalidCredentialsException::new);
    if (!passwordHasher.verify(password, customer.getAccount().getPasswordHash())) {
      throw new InvalidCredentialsException();
    }
    return customer;
  }
}

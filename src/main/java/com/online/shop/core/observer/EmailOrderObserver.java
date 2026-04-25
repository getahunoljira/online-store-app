package com.online.shop.core.observer;

import com.online.shop.model.Customer;
import com.online.shop.model.Order;
import com.online.shop.repository.CustomerRepository;
import com.online.shop.service.notification.EmailSender;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailOrderObserver implements OrderObserver {

  private static final Logger log = LoggerFactory.getLogger(EmailOrderObserver.class);

  private final EmailSender emailSender;
  private final CustomerRepository customerRepository;

  public EmailOrderObserver(
      Optional<EmailSender> emailSender, CustomerRepository customerRepository) {
    this.emailSender = emailSender.orElse(null);
    this.customerRepository = customerRepository;
  }

  @Override
  public void update(Order order) {
    if (emailSender == null) {
      return;
    }
    Optional<Customer> customer = customerRepository.findById(order.getCustomerId());
    if (customer.isEmpty()) {
      log.warn(
          "Skipping email for order {}: customer {} not found",
          order.getId(),
          order.getCustomerId());
      return;
    }
    String subject = "Order " + order.getId() + " update: " + order.getStatus();
    String body =
        "Hi " + customer.get().getName() + ", your order is now " + order.getStatus() + ".";
    try {
      emailSender.sendEmail(customer.get().getEmail(), subject, body);
    } catch (Exception e) {
      log.error(
          "Failed to send status email for order {} to {}: {}",
          order.getId(),
          customer.get().getEmail(),
          e.getMessage());
    }
  }
}

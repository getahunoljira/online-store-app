package com.online.shop.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.online.shop.domain.patterns.EmailOrderObserver;
import com.online.shop.model.Account;
import com.online.shop.model.Customer;
import com.online.shop.model.Order;
import com.online.shop.model.OrderStatus;
import com.online.shop.repository.CustomerRepository;
import com.online.shop.service.EmailSender;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EmailOrderObserverTest {

  private Customer makeCustomer() {
    return new Customer("cust-1", "Alice", "alice@example.com", new Account("alice", "secret"), null);
  }

  private Order makeOrder(OrderStatus status) {
    Order order =
        new Order(
            "order-1",
            "cust-1",
            List.of(),
            "1 St",
            "City",
            "ST",
            "00000",
            new BigDecimal("50.0"));
    order.setStatus(status);
    return order;
  }

  @Test
  void sendsEmailToCustomerOnTransition() {
    EmailSender sender = mock(EmailSender.class);
    CustomerRepository repo = mock(CustomerRepository.class);
    when(repo.findById("cust-1")).thenReturn(Optional.of(makeCustomer()));

    new EmailOrderObserver(Optional.of(sender), repo).update(makeOrder(OrderStatus.SHIPPED));

    verify(sender, times(1))
        .sendEmail(eq("alice@example.com"), contains("SHIPPED"), contains("SHIPPED"));
  }

  @Test
  void swallowsAndLogsSenderFailure() {
    EmailSender sender = mock(EmailSender.class);
    CustomerRepository repo = mock(CustomerRepository.class);
    when(repo.findById("cust-1")).thenReturn(Optional.of(makeCustomer()));
    doThrow(new RuntimeException("smtp down")).when(sender).sendEmail(any(), any(), any());

    new EmailOrderObserver(Optional.of(sender), repo).update(makeOrder(OrderStatus.SHIPPED));
    // no exception propagates to the caller
  }

  @Test
  void skipsWhenCustomerMissing() {
    EmailSender sender = mock(EmailSender.class);
    CustomerRepository repo = mock(CustomerRepository.class);
    when(repo.findById("cust-1")).thenReturn(Optional.empty());

    new EmailOrderObserver(Optional.of(sender), repo).update(makeOrder(OrderStatus.SHIPPED));

    verify(sender, never()).sendEmail(any(), any(), any());
  }
}

package com.online.shop.domain.patterns;

import com.online.shop.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingOrderObserver implements OrderObserver {

  private static final Logger log = LoggerFactory.getLogger(LoggingOrderObserver.class);

  @Override
  public void update(Order order) {
    log.info("Order {} transitioned to {}", order.getId(), order.getStatus());
  }
}

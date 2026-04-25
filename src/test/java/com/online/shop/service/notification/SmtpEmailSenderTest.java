package com.online.shop.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.online.shop.service.notification.SmtpEmailSender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class SmtpEmailSenderTest {

  @Test
  void populatesAllFieldsOnSend() {
    JavaMailSender jms = mock(JavaMailSender.class);
    SmtpEmailSender sender = new SmtpEmailSender(jms, "shop@example.com");

    sender.sendEmail("ada@example.com", "Order update", "Hi Ada, your order is SHIPPED.");

    ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(jms).send(captor.capture());
    SimpleMailMessage msg = captor.getValue();
    assertThat(msg.getFrom()).isEqualTo("shop@example.com");
    assertThat(msg.getTo()).containsExactly("ada@example.com");
    assertThat(msg.getSubject()).isEqualTo("Order update");
    assertThat(msg.getText()).isEqualTo("Hi Ada, your order is SHIPPED.");
  }
}

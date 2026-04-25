package com.online.shop.service.notification;

import com.online.shop.service.notification.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(JavaMailSender.class)
public class SmtpEmailSender implements EmailSender {

  private final JavaMailSender mailSender;
  private final String from;

  public SmtpEmailSender(
      JavaMailSender mailSender,
      @Value("${app.email.from:no-reply@shop.local}") String from) {
    this.mailSender = mailSender;
    this.from = from;
  }

  @Override
  public void sendEmail(String to, String subject, String body) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(from);
    msg.setTo(to);
    msg.setSubject(subject);
    msg.setText(body);
    mailSender.send(msg);
  }
}

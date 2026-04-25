package com.online.shop.service.notification;

public interface EmailSender {
  void sendEmail(String to, String subject, String body);
}

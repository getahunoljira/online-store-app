package com.online.shop.service;

public interface EmailSender {
  void sendEmail(String to, String subject, String body);
}

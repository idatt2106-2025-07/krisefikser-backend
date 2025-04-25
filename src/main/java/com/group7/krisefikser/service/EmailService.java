package com.group7.krisefikser.service;

import com.group7.krisefikser.enums.EmailTemplateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;
  @Autowired
  private EmailTemplateService emailTemplateService;

  public void sendSimpleMessage(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("krisefikser@gmail.com");
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    mailSender.send(message);
  }

  public void sendTemplateMessage(String to, EmailTemplateType type, Map<String, String> params) {
    String subject = emailTemplateService.getSubject(type);
    String body = emailTemplateService.getBody(type, params);
    sendSimpleMessage(to, subject, body);
  }
}
package com.group7.krisefikser.service;

import com.group7.krisefikser.enums.EmailTemplateType;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service class for sending emails.
 * This class uses JavaMailSender to send simple and template-based emails.
 */
@Service
public class EmailService {

  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

  @Autowired
  JavaMailSender mailSender;
  @Autowired
  EmailTemplateService emailTemplateService;

  /**
   * Sends a simple email message.
   *
   * @param to      The recipient's email address.
   * @param subject The subject of the email.
   * @param text    The body of the email.
   */
  @Async
  public void sendSimpleMessage(String to, String subject, String text) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom("krisefikser@gmail.com");
      message.setTo(to);
      message.setSubject(subject);
      message.setText(text);
      mailSender.send(message);
      logger.info("Email sent to {}", to);
    } catch (Exception e) {
      logger.error("Failed to send email to {}: {}", to, e.getMessage());
    }
  }

  /**
   * Sends a template-based email message.
   *
   * @param to    The recipient's email address.
   * @param type  The type of the email template.
   * @param params The parameters to be used in the template.
   */
  public void sendTemplateMessage(String to, EmailTemplateType type, Map<String, String> params) {
    String subject = emailTemplateService.getSubject(type);
    String body = emailTemplateService.getBody(type, params);
    sendSimpleMessage(to, subject, body);
  }
}
package com.group7.krisefikser.service.other;

import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.service.other.EmailService;
import com.group7.krisefikser.service.other.EmailTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

  private EmailService emailService;
  private JavaMailSender mailSender;
  private EmailTemplateService emailTemplateService;

  @BeforeEach
  void setUp() {
    mailSender = mock(JavaMailSender.class);
    emailTemplateService = mock(EmailTemplateService.class);
    emailService = new EmailService();
    emailService.mailSender = mailSender;
    emailService.emailTemplateService = emailTemplateService;
  }

  @Test
  void testSendSimpleMessage() {
    // Arrange
    String to = "test@example.com";
    String subject = "Test Subject";
    String text = "This is a test message.";

    // Act
    emailService.sendSimpleMessage(to, subject, text);

    // Assert
    ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender, times(1)).send(messageCaptor.capture());
    SimpleMailMessage sentMessage = messageCaptor.getValue();

    assertEquals("krisefikser@gmail.com", sentMessage.getFrom());
    assertEquals(to, sentMessage.getTo()[0]);
    assertEquals(subject, sentMessage.getSubject());
    assertEquals(text, sentMessage.getText());
  }

  @Test
  void testSendTemplateMessage() {
    // Arrange
    String to = "test@example.com";
    EmailTemplateType type = EmailTemplateType.PASSWORD_RESET;
    Map<String, String> params = Map.of("resetLink", "http://example.com/reset");

    when(emailTemplateService.getSubject(type)).thenReturn("Password Reset Request");
    when(emailTemplateService.getBody(type, params)).thenReturn("Reset your password at: http://example.com/reset");

    // Act
    emailService.sendTemplateMessage(to, type, params);

    // Assert
    ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender, times(1)).send(messageCaptor.capture());
    SimpleMailMessage sentMessage = messageCaptor.getValue();

    assertEquals("krisefikser@gmail.com", sentMessage.getFrom());
    assertEquals(to, sentMessage.getTo()[0]);
    assertEquals("Password Reset Request", sentMessage.getSubject());
    assertEquals("Reset your password at: http://example.com/reset", sentMessage.getText());

    verify(emailTemplateService, times(1)).getSubject(type);
    verify(emailTemplateService, times(1)).getBody(type, params);
  }
}

package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.EmailRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

  @InjectMocks
  private EmailService emailService;

  @Mock
  private JavaMailSender mailSender;

  @Test
  public void testSendSimpleMessage() {
    // Arrange
    String to = "test@example.com";
    String subject = "Test Subject";
    String text = "Hello, world!";

    // Act
    emailService.sendSimpleMessage(to, subject, text);

    // Assert
    ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender, times(1)).send(captor.capture());

    SimpleMailMessage sentMessage = captor.getValue();
    assertEquals(to, sentMessage.getTo()[0]);
    assertEquals(subject, sentMessage.getSubject());
    assertEquals(text, sentMessage.getText());
    assertEquals("krisefikser@gmail.com", sentMessage.getFrom());

  }
}

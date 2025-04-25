package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.EmailRequest;
import com.group7.krisefikser.dto.request.EmailTemplateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.group7.krisefikser.service.EmailService;

/**
 * This class is responsible for handling email-related requests.
 *
 */
@RestController
@RequestMapping("/api/auth/email")
public class EmailController {

  @Autowired
  private EmailService emailService;

  /**
   * Endpoint to send a simple email.
   *
   * @param emailRequest The request containing the recipient's email, subject, and text.
   * @return A response indicating the success or failure of the operation.
   */
  @PostMapping("/send")
  public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
    try {
      emailService.sendSimpleMessage(
          emailRequest.getTo(),
          emailRequest.getSubject(),
          emailRequest.getText()
      );
      return ResponseEntity.ok("Email sent successfully!");
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
    }

  }
  /**
   * Endpoint to send a template email.
   *
   * @param request The request containing the recipient's email, template type, and parameters.
   * @return A response indicating the success or failure of the operation.
   */
  @PostMapping("/send-template")
  public ResponseEntity<String> sendTemplateEmail(@RequestBody EmailTemplateRequest request) {
    try {
      emailService.sendTemplateMessage(
          request.getTo(),
          request.getType(),
          request.getParams()
      );
      return ResponseEntity.ok("Template email sent successfully!");

    } catch (Exception e) {
      return ResponseEntity.status(500).body("Failed to send template email: " + e.getMessage());
    }
  }
}
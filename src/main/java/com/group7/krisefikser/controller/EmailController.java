package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.group7.krisefikser.service.EmailService;

/**
 * This class is responsible for handling email-related requests.
 *
 */
@RestController
@RequestMapping("/api/email")
public class EmailController {

  @Autowired
  private EmailService emailService;

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

}
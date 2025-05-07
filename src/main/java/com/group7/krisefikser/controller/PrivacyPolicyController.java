package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.UpdateRegisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.request.UpdateUnregisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.response.GetRegisteredPrivacyPolicyResponse;
import com.group7.krisefikser.dto.response.GetUnregisteredPrivacyPolicyResponse;
import com.group7.krisefikser.service.PrivacyPolicyService;
import com.group7.krisefikser.utils.ValidationUtils;
import jakarta.validation.Valid;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/privacy-policy")
@RequiredArgsConstructor
public class PrivacyPolicyController {

  private final PrivacyPolicyService privacyPolicyService;

  Logger logger = Logger.getLogger(PrivacyPolicyController.class.getName());

  @GetMapping("/registered")
  public ResponseEntity<?> getRegisteredPrivacyPolicy() {
    logger.info("Fetching registered privacy policy");

    try {
      GetRegisteredPrivacyPolicyResponse response = privacyPolicyService.getRegisteredPrivacyPolicy();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.severe("Error fetching registered privacy policy: " + e.getMessage());
      return ResponseEntity.status(500).body("Error fetching registered privacy policy");
    }
  }

  @GetMapping("/unregistered")
  public ResponseEntity<?> getUnregisteredPrivacyPolicy() {
    logger.info("Fetching unregistered privacy policy");
    try {
      GetUnregisteredPrivacyPolicyResponse response = privacyPolicyService.getUnregisteredPrivacyPolicy();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.severe("Error fetching unregistered privacy policy: " + e.getMessage());
      return ResponseEntity.status(500).body("Error fetching unregistered privacy policy");
    }
  }

  @PostMapping("/registered")
  public ResponseEntity<?> updateRegisteredPrivacyPolicy(
      @RequestBody @Valid UpdateRegisteredPrivacyPolicyRequest request,
      BindingResult bindingResult) {
    logger.info("Updating registered privacy policy");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      privacyPolicyService.updateRegisteredPrivacyPolicy(request);
      return ResponseEntity.ok("Registered privacy policy updated successfully");
    } catch (Exception e) {
      logger.severe("Error updating registered privacy policy: " + e.getMessage());
      return ResponseEntity.status(500).body("Error updating registered privacy policy");
    }
  }

  @PostMapping("/unregistered")
  public ResponseEntity<?> updateUnregisteredPrivacyPolicy(
      @RequestBody @Valid UpdateUnregisteredPrivacyPolicyRequest request,
      BindingResult bindingResult) {
    logger.info("Updating unregistered privacy policy");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      privacyPolicyService.updateUnregisteredPrivacyPolicy(request);
      return ResponseEntity.ok("Unregistered privacy policy updated successfully");
    } catch (Exception e) {
      logger.severe("Error updating unregistered privacy policy: " + e.getMessage());
      return ResponseEntity.status(500).body("Error updating unregistered privacy policy");
    }
  }
}

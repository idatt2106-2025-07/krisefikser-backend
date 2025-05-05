package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.SharePositionRequest;
import com.group7.krisefikser.service.UserPositionService;
import com.group7.krisefikser.utils.ValidationUtils;
import jakarta.validation.Valid;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/position")
@RequiredArgsConstructor
public class UserPositionController {

  private final UserPositionService positionService;

  private static final Logger logger = Logger.getLogger(UserPositionController.class.getName());

  @PostMapping("/share")
  public ResponseEntity<?> sharePosition(
      @RequestBody @Valid SharePositionRequest request, BindingResult bindingResult) {
    logger.info("Received request to share position");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      positionService.sharePosition(request);
      logger.info("Position shared successfully");
      return ResponseEntity.ok("Position shared successfully");
    } catch (Exception e) {
      logger.severe("Error sharing position: " + e.getMessage());
      return ResponseEntity.status(500).body("Error sharing position");
    }
  }
}

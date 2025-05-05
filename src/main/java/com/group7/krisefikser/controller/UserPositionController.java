package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.SharePositionRequest;
import com.group7.krisefikser.dto.response.HouseholdMemberPositionResponse;
import com.group7.krisefikser.service.UserPositionService;
import com.group7.krisefikser.utils.ValidationUtils;
import jakarta.validation.Valid;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/position")
@RequiredArgsConstructor
public class UserPositionController {

  private final UserPositionService userPositionService;

  private static final Logger logger = Logger.getLogger(UserPositionController.class.getName());

  @PostMapping("/share")
  public ResponseEntity<?> sharePosition(
      @RequestBody @Valid SharePositionRequest request, BindingResult bindingResult) {
    logger.info("Received request to share position");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      userPositionService.sharePosition(request);
      logger.info("Position shared successfully");
      return ResponseEntity.ok("Position shared successfully");
    } catch (Exception e) {
      logger.severe("Error sharing position: " + e.getMessage());
      return ResponseEntity.status(500).body("Error sharing position");
    }
  }

  @DeleteMapping("/delete")
  public ResponseEntity<?> stopSharingPosition() {
    logger.info("Received request to stop sharing position");

    try {
      userPositionService.deleteUserPosition();
      logger.info("Stopped sharing position successfully");
      return ResponseEntity.ok("Stopped sharing position successfully");
      } catch (Exception e) {
      logger.severe("Error stopping sharing position: " + e.getMessage());
      return ResponseEntity.status(500).body("Error stopping sharing position");
    }
  }

  @GetMapping("/household")
  public ResponseEntity<?> getHouseholdPosition() {
    logger.info("Received request to get household position");

    try {
      HouseholdMemberPositionResponse[] householdMemberPositionResponses =
          userPositionService.getHouseholdPositions();
      logger.info("Household positions retrieved successfully");
      return ResponseEntity.ok(householdMemberPositionResponses);
    } catch (Exception e) {
      logger.severe("Error retrieving household positions: " + e.getMessage());
      return ResponseEntity.status(500).body("Error retrieving household positions");
    }
  }
}

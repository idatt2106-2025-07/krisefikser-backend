package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.response.AffectedAreaResponse;
import com.group7.krisefikser.service.AffectedAreaService;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling requests related to affected areas.
 * This class provides endpoints to fetch and manage affected areas.
 */
@RestController
@RequestMapping("/api/affected-area")
@RequiredArgsConstructor
public class AffectedAreaController {
  private final AffectedAreaService affectedAreaService;

  private static final Logger logger = Logger.getLogger(AffectedAreaController.class.getName());

  /**
   * Endpoint to fetch all affected areas.
   *
   * @return a list of affected areas
   */
  @GetMapping
  public ResponseEntity<List<AffectedAreaResponse>> getAllAffectedAreas() {
    logger.info("Fetching all affected areas");
    try {
      List<AffectedAreaResponse> affectedAreas = affectedAreaService.getAllAffectedAreas();
      logger.info("Successfully fetched all affected areas");
      return ResponseEntity.ok(affectedAreas);
    } catch (Exception e) {
      logger.severe("Error fetching affected areas: " + e.getMessage());
      return ResponseEntity.status(500).body(List.of());
    }
  }
}

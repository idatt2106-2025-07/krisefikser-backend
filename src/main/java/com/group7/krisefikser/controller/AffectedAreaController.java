package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.response.AffectedAreaResponse;
import com.group7.krisefikser.service.AffectedAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Affected Area", description = "Endpoints for managing affected areas")
public class AffectedAreaController {
  private final AffectedAreaService affectedAreaService;

  private static final Logger logger = Logger.getLogger(AffectedAreaController.class.getName());

  /**
   * Endpoint to fetch all affected areas.
   *
   * @return a list of affected areas
   */
  @Operation(
          summary = "Get all affected areas",
          description = "Retrieves a list of all affected areas.",
          responses = {
            @ApiResponse(responseCode = "200", description =
                          "Successfully retrieved all affected areas",
                          content = @Content(mediaType = "application/json",
                                  schema = @Schema(implementation = AffectedAreaResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
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

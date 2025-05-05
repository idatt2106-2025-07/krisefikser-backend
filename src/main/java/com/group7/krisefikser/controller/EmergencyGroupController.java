package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.EmergencyGroupRequest;
import com.group7.krisefikser.dto.response.EmergencyGroupResponse;
import com.group7.krisefikser.dto.response.ErrorResponse;
import com.group7.krisefikser.service.EmergencyGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling HTTP requests related to emergency groups.
 * This class contains endpoints for retrieving and adding emergency groups.
 * It uses the EmergencyGroupService to perform the operations.
 */
@RestController
@RequestMapping("/api/emergency-groups")
@RequiredArgsConstructor
@Tag(name = "Emergency Group", description = "Endpoints for managing emergency groups")
public class EmergencyGroupController {
  private final EmergencyGroupService emergencyGroupService;
  private final Logger logger = LoggerFactory.getLogger(EmergencyGroupController.class);

  /**
   * Retrieves the EmergencyGroup object with the specified ID from the repository.
   *
   * @param id the ID of the EmergencyGroup to retrieve
   * @return the EmergencyGroupResponse object with the specified ID
   */
  @Operation(
          summary = "Get Emergency Group by ID",
          description = "Retrieve an emergency group by its ID.",
          parameters = {
            @Parameter(name = "id", description = "ID of the emergency group to retrieve")
          },
          responses = {
            @ApiResponse(responseCode = "200",
                    description = "Emergency group retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmergencyGroupResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Emergency group not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
          }
  )
  @GetMapping("/{id}")
  public ResponseEntity<Object> getEmergencyGroupById(@PathVariable Long id) {
    try {
      EmergencyGroupResponse response = emergencyGroupService.getEmergencyGroupById(id);
      logger.info("Emergency group with ID {} retrieved successfully.", id);
      return ResponseEntity.ok(response);
    } catch (NoSuchElementException e) {
      logger.error("Emergency group with ID {} not found.", id);
      return ResponseEntity.status(404).body(new ErrorResponse("Emergency group not found. "
              + "The emergency group with the specified ID does not exist."
      ));
    } catch (Exception e) {
      logger.error("An error occurred while retrieving emergency group with ID {}: {}",
              id, e.getMessage());
      return ResponseEntity.status(500).body("An error occurred while retrieving the "
              + "emergency group.");
    }
  }

  /**
   * Adds a new EmergencyGroup to the repository.
   *
   * @param request the EmergencyGroup object to add
   * @return the EmergencyGroupResponse object representing the added group
   */
  @PostMapping
  public ResponseEntity<Object> addEmergencyGroup(@RequestBody EmergencyGroupRequest request) {
    try {
      EmergencyGroupResponse response = emergencyGroupService.addEmergencyGroup(request);
      logger.info("Emergency group {} added successfully.", response.getName());
      return ResponseEntity.status(201).body(response);
    } catch (IllegalArgumentException e) {
      logger.error("Failed to add emergency group: {}", e.getMessage());
      return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
      logger.error("An error occurred while adding the emergency group: {}", e.getMessage());
      return ResponseEntity.status(500).body("An error occurred while adding the emergency group.");
    }
  }
}

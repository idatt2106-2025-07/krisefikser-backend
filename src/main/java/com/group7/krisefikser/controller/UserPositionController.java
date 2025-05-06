package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.SharePositionRequest;
import com.group7.krisefikser.dto.response.GroupMemberPositionResponse;
import com.group7.krisefikser.dto.response.HouseholdMemberPositionResponse;
import com.group7.krisefikser.service.UserPositionService;
import com.group7.krisefikser.utils.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class for handling requests related to user positions.
 * This class provides endpoints to share and stop sharing position.
 */
@Controller
@RequestMapping("/api/position")
@Tag(name = "User Position", description = "Endpoints for sharing and managing user positions")
@RequiredArgsConstructor
public class UserPositionController {

  private final UserPositionService userPositionService;

  private static final Logger logger = Logger.getLogger(UserPositionController.class.getName());

  /**
   * Endpoint to share a user's position.
   * This endpoint will accept a request containing the user's position details.
   *
   * @param request The request containing the user's position details.
   * @param bindingResult The result of the validation
   * @return ResponseEntity indicating the result of the operation.
   */
  @Operation(
      summary = "Share user's position",
      description = "Allows a user to share their current geographic position "
          + "(latitude and longitude).",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          description = "User's position including latitude and longitude",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = SharePositionRequest.class)
          )
      ),
      responses = {
          @ApiResponse(responseCode = "200", description = "Position shared successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid latitude or longitude values",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
          @ApiResponse(responseCode = "500", description = "Internal server error",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      }
  )
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

  /**
   * Endpoint to delete a user's position.
   * This endpoint will stop sharing the user's position.
   *
   * @return ResponseEntity indicating the result of the operation.
   */
  @Operation(
      summary = "Stop sharing user's position",
      description = "Deletes the user's shared position, "
          + "effectively stopping the location sharing.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Stopped sharing position successfully"),
          @ApiResponse(responseCode = "500", description = "Internal server error",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      }
  )
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

  /**
   * Endpoint to get the position of household members.
   * This endpoint will return the positions of all household members.
   *
   * @return ResponseEntity containing the positions of household members.
   */
  @Operation(
      summary = "Get positions of household members",
      description = "Retrieves the current positions of all members in the user's household.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully retrieved household member positions",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                      schema = @Schema(implementation = HouseholdMemberPositionResponse.class)
                  )
              )
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
          )
      }
  )
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

  /**
   * Endpoint to get the position of group members.
   * This endpoint will return the positions of all group members.
   *
   * @return ResponseEntity containing the positions of group members.
   */
  @Operation(
      summary = "Get positions of group members",
      description = "Retrieves the current positions of all members in the user's group.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully retrieved group member positions",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                      schema = @Schema(implementation = GroupMemberPositionResponse.class)
                  )
              )
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
          )
      }
  )
  @GetMapping("/group")
  public ResponseEntity<?> getGroupPosition() {
    logger.info("Received request to get group position");

    try {
      GroupMemberPositionResponse[] groupMemberPositionResponses =
          userPositionService.getGroupPositions();
      logger.info("Group positions retrieved successfully");
      return ResponseEntity.ok(groupMemberPositionResponses);
    } catch (Exception e) {
      logger.severe("Error retrieving group positions: " + e.getMessage());
      return ResponseEntity.status(500).body("Error retrieving group positions");
    }
  }
}

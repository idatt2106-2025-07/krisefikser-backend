package com.group7.krisefikser.controller.location;

import com.group7.krisefikser.dto.request.location.AffectedAreaRequest;
import com.group7.krisefikser.dto.response.location.AffectedAreaResponse;
import com.group7.krisefikser.dto.response.other.ErrorResponse;
import com.group7.krisefikser.service.location.AffectedAreaService;
import com.group7.krisefikser.utils.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

  /**
   * Endpoint to add a new affected area.
   *
   * @param request the request object containing details of the affected area to be added.
   * @return the response entity containing the added affected area details
   */
  @Operation(
          summary = "Add a new affected area",
          description = "Adds a new affected area to the system.",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Affected area details",
                  required = true,
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = AffectedAreaRequest.class))
          ),
          responses = {
            @ApiResponse(responseCode = "201", description =
                    "Successfully added a new affected area",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AffectedAreaResponse.class))),
            @ApiResponse(responseCode = "400", description =
                    "Invalid input provided",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description =
                    "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
          }
  )
  @PostMapping
  public ResponseEntity<Object> addAffectedArea(
          @Valid @RequestBody AffectedAreaRequest request,
          BindingResult bindingResult
  ) {
    logger.info("Adding a new affected area");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      AffectedAreaResponse addedArea = affectedAreaService.addAffectedArea(request);
      URI location = ServletUriComponentsBuilder.fromCurrentRequest()
              .path("/{id}")
              .buildAndExpand(addedArea.getId())
              .toUri();
      logger.info("Successfully added a new affected area");
      return ResponseEntity.created(location).body(addedArea);
    } catch (IllegalStateException e) {
      logger.severe(e.getMessage());
      return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
      logger.severe("Error adding affected area: " + e.getMessage());
      return ResponseEntity.status(500).body("Error adding affected area");
    }
  }

  /**
   * Endpoint to delete an affected area by ID.
   *
   * @param id the ID of the affected area to be deleted
   * @return a response entity indicating the result of the deletion
   */
  @Operation(
          summary = "Delete an affected area",
          description = "Deletes an affected area by its ID.",
          parameters = {
            @Parameter(
                    name = "id", in = ParameterIn.PATH,
                    description = "ID of the affected area to be deleted",
                    schema = @Schema(type = "integer", format = "int64"))
          },
          responses = {
            @ApiResponse(responseCode = "204", description =
                    "Successfully deleted the affected area"),
            @ApiResponse(responseCode = "404", description =
                    "Affected area not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description =
                    "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
          }
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteAffectedArea(@PathVariable Long id) {
    logger.info("Delete request registered for affected area with ID: " + id);
    try {
      affectedAreaService.deleteAffectedArea(id);
      logger.info("Successfully deleted affected area with ID: " + id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      logger.severe(e.getMessage());
      return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
      logger.severe("Error deleting affected area: " + e.getMessage());
      return ResponseEntity.status(500).body("A server error occurred while deleting "
              + "affected area");
    }
  }

  /**
   * Endpoint to update an existing affected area.
   *
   * @param id      the ID of the affected area to be updated
   * @param request the request object containing updated details of the affected area
   * @return the response entity containing the updated affected area details
   */
  @Operation(
          summary = "Update an affected area",
          description = "Updates an existing affected area.",
          parameters = {
            @Parameter(
                    name = "id", in = ParameterIn.PATH,
                    description = "ID of the affected area to be updated",
                    schema = @Schema(type = "integer", format = "int64"))
          },
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Updated affected area details",
                  required = true,
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = AffectedAreaRequest.class))
          ),
          responses = {
            @ApiResponse(responseCode = "200", description =
                    "Successfully updated the affected area",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =
                                    AffectedAreaResponse.class))),
            @ApiResponse(responseCode = "400", description =
                    "Invalid input provided",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =
                                    ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description =
                    "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =
                                    ErrorResponse.class)))
          }
  )
  @PutMapping("/{id}")
  public ResponseEntity<Object> updateAffectedArea(
          @PathVariable Long id,
          @Valid @RequestBody AffectedAreaRequest request,
          BindingResult bindingResult
  ) {
    logger.info("Updating affected area with ID: " + id);

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      AffectedAreaResponse updatedArea = affectedAreaService.updateAffectedArea(id, request);
      logger.info("Successfully updated affected area with ID: " + id);
      return ResponseEntity.ok(updatedArea);
    } catch (IllegalStateException e) {
      logger.severe(e.getMessage());
      return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
      logger.severe("Error updating affected area: " + e.getMessage());
      return ResponseEntity.status(500).body("Error updating affected area");
    }
  }
}

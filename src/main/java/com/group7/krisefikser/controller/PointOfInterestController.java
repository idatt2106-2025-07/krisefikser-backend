package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.GetPointsOfInterestRequest;
import com.group7.krisefikser.dto.request.PointOfInterestRequest;
import com.group7.krisefikser.dto.response.ErrorResponse;
import com.group7.krisefikser.dto.response.PointOfInterestResponse;
import com.group7.krisefikser.enums.PointOfInterestType;
import com.group7.krisefikser.service.PointOfInterestService;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


/**
 * Controller class for handling requests related to points of interest.
 * This class will contain endpoints for retrieving points of interest based on type.
 * It will use the PointOfInterestService to perform the necessary operations.
 */
@RestController
@RequestMapping("/api/point-of-interest")
@Tag(name = "Point of Interest", description = "Endpoints for managing points of interest")
public class PointOfInterestController {
  private final PointOfInterestService pointOfInterestService;

  private static final Logger logger = Logger.getLogger(PointOfInterestController.class.getName());

  /**
   * Constructor for PointOfInterestController.
   * This constructor is used for dependency injection of the PointOfInterestService.
   *
   * @param pointOfInterestService The service to be injected.
   */
  @Autowired
  public PointOfInterestController(PointOfInterestService pointOfInterestService) {
    this.pointOfInterestService = pointOfInterestService;
  }

  /**
   * Endpoint to get points of interest based on type.
   * This endpoint will accept a request containing a list of point of interest types
   *
   * @param request The request containing the types of points of interest to be retrieved.
   * @return ResponseEntity containing a list of PointOfInterestResponse objects.
   */
  @Operation(
          summary = "Get points of interest by types",
          description = "Retrieves a list of points of interest based on the "
                  + "provided types in the query parameters.",
          parameters = @Parameter(
                  name = "types",
                  description = "List of point of interest types to filter by. "
                          + "Valid types are: shelter, food_central, water_station, "
                          + "defibrillator, hospital. Multiple types can be provided "
                          + "by repeating the 'types' parameter in the URL "
                          + "(e.g., /api/poi?types=shelter&types=food_central).",
                  required = true,
                  schema = @Schema(type = "array",
                          enumAsRef = true,
                          implementation = PointOfInterestType.class)
          ),
          responses = {
                  @ApiResponse(responseCode = "200",
                          description = "Successfully retrieved points of interest",
                          content = @Content(mediaType = "application/json",
                                  schema = @Schema(implementation =
                                          PointOfInterestResponse.class))),
                  @ApiResponse(responseCode = "400",
                          description = "Invalid point of interest type provided",
                          content = @Content(mediaType = "application/json",
                                  schema = @Schema(implementation = List.class))),
                  @ApiResponse(responseCode = "500",
                          description = "Internal server error",
                          content = @Content(mediaType = "application/json",
                                  schema = @Schema(implementation = List.class)))
          }
  )
  @GetMapping
  public ResponseEntity<List<PointOfInterestResponse>> getPointsOfInterest(
          @ModelAttribute GetPointsOfInterestRequest request) {
    logger.info("Received request to get points of interest with types: "
            + request.getTypes());
    try {
      List<PointOfInterestResponse> pointsOfInterest = pointOfInterestService
              .getPointsOfInterestByTypes(request);
      logger.info("Successfully retrieved points of interest");
      return ResponseEntity.ok(pointsOfInterest);
    } catch (IllegalArgumentException e) {
      logger.info("Error retrieving points of interest: " + e.getMessage());
      return ResponseEntity.badRequest().body(List.of());
    } catch (Exception e) {
      logger.severe("Unexpected error: " + e.getMessage());
      return ResponseEntity.status(500).body(List.of());
    }
  }

  /**
   * Endpoint to add a new point of interest.
   * This endpoint will accept a request containing the details of the point of
   * interest to be added.
   *
   * @param pointOfInterestRequest The request containing the details of the
   *                               point of interest to be added.
   * @return ResponseEntity containing the added PointOfInterestResponse object.
   */
  @Operation(
          summary = "Add a new point of interest",
          description = "Creates a new point of interest based on the provided details.",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  required = true,
                  description = "Details of the point of interest to add",
                  content = @Content(
                          mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(implementation = PointOfInterestRequest.class)
                  )
          ),
          responses = {
                  @ApiResponse(responseCode = "201", description =
                          "Successfully created the new point of interest",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  schema = @Schema(implementation = PointOfInterestResponse.class))),
                  @ApiResponse(responseCode = "400", description = "Invalid request payload",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  schema = @Schema(implementation = ErrorResponse.class))),
                  @ApiResponse(responseCode = "500", description = "Internal server error",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  schema = @Schema(implementation = ErrorResponse.class)))
          }
  )
  @PostMapping
  public ResponseEntity<Object> addPointOfInterest(
          @Valid @RequestBody PointOfInterestRequest pointOfInterestRequest,
          BindingResult bindingResult) {
    logger.info("Received request to add a new point of interest");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      PointOfInterestResponse addedPoint = pointOfInterestService
              .addPointOfInterest(pointOfInterestRequest);
      URI location = ServletUriComponentsBuilder.fromCurrentRequest()
              .path("/{id}")
              .buildAndExpand(addedPoint.getId())
              .toUri();
      logger.info("Successfully added point of interest");
      return ResponseEntity.created(location).body(addedPoint);
    } catch (IllegalArgumentException e) {
      logger.info("Error adding point of interest: " + e.getMessage());
      return ResponseEntity.badRequest().body(new ErrorResponse(
              "Invalid point of interest details provided: " + e.getMessage()
      ));
    } catch (Exception e) {
      logger.severe("Unexpected error: " + e.getMessage());
      return ResponseEntity.status(500).body(new ErrorResponse(
              "Internal server error while adding point of interest: " + e.getMessage()
      ));
    }
  }

  /**
   * Endpoint to delete a point of interest.
   * This endpoint will accept a request containing the ID of the point of interest to be deleted.
   *
   * @param id The ID of the point of interest to be deleted.
   * @return ResponseEntity indicating the result of the deletion operation.
   */
  @Operation(
          summary = "Delete a point of interest",
          description = "Deletes a specific point of interest identified by its ID.",
          parameters = {
                  @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                          description = "ID of the point of interest to delete",
                          schema = @Schema(type = "integer", format = "int64"))
          },
          responses = {
                  @ApiResponse(responseCode = "204",
                          description = "Successfully deleted the point of interest"),
                  @ApiResponse(responseCode = "400",
                          description = "Invalid point of interest ID provided",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  schema = @Schema(implementation = ErrorResponse.class))),
                  @ApiResponse(responseCode = "500", description = "Internal server error",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  schema = @Schema(implementation = ErrorResponse.class)))
          }
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deletePointOfInterest(@PathVariable Long id) {
    logger.info("Received request to delete point of interest with ID: " + id);
    try {
      pointOfInterestService.deletePointOfInterest(id);
      logger.info("Successfully deleted point of interest with ID: " + id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      logger.info("Error deleting point of interest: " + e.getMessage());
      return ResponseEntity.badRequest().body(new ErrorResponse(
              "Invalid point of interest ID provided: " + e.getMessage()
      ));
    } catch (Exception e) {
      logger.severe("Unexpected error: " + e.getMessage());
      return ResponseEntity.status(500).body(new ErrorResponse(
              "Internal server error while deleting point of interest: " + e.getMessage()
      ));
    }
  }

  /**
   * Endpoint to update an existing point of interest.
   * This endpoint will accept a request containing the ID of the point of interest
   * to be updated and the new details for the point of interest.
   *
   * @param id                     The ID of the point of interest to be updated.
   * @param pointOfInterestRequest The request containing the new details for the
   *                               point of interest.
   * @return ResponseEntity containing the updated PointOfInterestResponse object.
   */
  @Operation(
          summary = "Update an existing point of interest",
          description = "Updates the details of a specific point of interest identified by its ID.",
          parameters = {
                  @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                          description = "ID of the point of interest to update",
                          schema = @Schema(type = "integer", format = "int64")),
          },
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  required = true,
                  description = "Point of interest details to be updated",
                  content = @Content(
                          mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(implementation = PointOfInterestRequest.class)
                  )
          ),
          responses = {
                  @ApiResponse(responseCode = "200",
                          description = "Successfully updated the point of interest",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  schema = @Schema(implementation = PointOfInterestResponse.class))),
                  @ApiResponse(responseCode = "400",
                          description = "Invalid request payload or parameters",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  schema = @Schema(implementation = ErrorResponse.class))),
                  @ApiResponse(responseCode = "500", description = "Internal server error",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  schema = @Schema(implementation = ErrorResponse.class)))
          }
  )
  @PutMapping("/{id}")
  public ResponseEntity<Object> updatePointOfInterest(
          @PathVariable Long id,
          @Valid @RequestBody PointOfInterestRequest pointOfInterestRequest,
          BindingResult bindingResult) {
    logger.info("Received request to update point of interest with ID: " + id);

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      PointOfInterestResponse updatedPoint = pointOfInterestService
              .updatePointOfInterest(id, pointOfInterestRequest);
      logger.info("Successfully updated point of interest with ID: " + id);
      return ResponseEntity.ok(updatedPoint);
    } catch (IllegalArgumentException e) {
      logger.info("Error updating point of interest: " + e.getMessage());
      return ResponseEntity.badRequest().body(new ErrorResponse(
              "Invalid point of interest details provided: " + e.getMessage()
      ));
    } catch (Exception e) {
      logger.severe("Unexpected error: " + e.getMessage());
      return ResponseEntity.status(500).body(new ErrorResponse(
              "Internal server error while updating point of interest: " + e.getMessage()
      ));
    }
  }
}

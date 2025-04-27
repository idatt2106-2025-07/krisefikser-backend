package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.GetPointsOfInterestRequest;
import com.group7.krisefikser.dto.response.PointOfInterestResponse;
import com.group7.krisefikser.service.PointOfInterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * Controller class for handling requests related to points of interest.
 * This class will contain endpoints for retrieving points of interest based on type.
 * It will use the PointOfInterestService to perform the necessary operations.
 */
@RestController
@RequestMapping("/api/point-of-interest")
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
  @GetMapping
  public ResponseEntity<List<PointOfInterestResponse>> getPointsOfInterest(
          @RequestBody GetPointsOfInterestRequest request) {
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
}

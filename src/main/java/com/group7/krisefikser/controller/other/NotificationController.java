package com.group7.krisefikser.controller.other;

import com.group7.krisefikser.dto.request.other.NotificationRequest;
import com.group7.krisefikser.dto.response.other.NotificationResponse;
import com.group7.krisefikser.service.other.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling notifications.
 * The role of this controller is to manage the
 * HTTP requests related to notifications,
 * such as retrieving incident notifications.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
@Tag(name = "Notifications", description = "Notification management")
public class NotificationController {

  private final NotificationService notificationService;
  private static final Logger logger = Logger.getLogger(NotificationController.class.getName());

  /**
   * Endpoint to get incident notifications.
   * Handles GET requests to "/api/notification/incidents"
   *
   * @return ResponseEntity containing a list of NotificationResponse objects
   */
  @Operation(
      summary = "Get incident notifications",
      description = "Retrieves a list of high severity incident notifications"
          + " that require immediate attention.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully retrieved incident notifications",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = NotificationResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error occurred while retrieving notifications",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @PostMapping("/incidents")
  public ResponseEntity<List<NotificationResponse>> getIncidentsNotifications(@RequestBody
                        NotificationRequest request) {
    logger.info("Retrieving incident notifications");
    double latitude = request.getLatitude();
    double longitude = request.getLongitude();
    try {
      List<NotificationResponse> notifications =
          notificationService.getIncidentsNotification(latitude, longitude);
      logger.info("Retrieved " + notifications.size() + " incident notifications");
      return ResponseEntity.ok(notifications);
    } catch (Exception e) {
      logger.severe("Error retrieving incident notifications: " + e.getMessage());
      return ResponseEntity.status(500).body(null);
    }
  }
}
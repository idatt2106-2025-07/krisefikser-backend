package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.response.UserInfoResponse;
import com.group7.krisefikser.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class for handling user-related operations.
 * Provides endpoints for fetching user profile information.
 */
@Controller
@RequestMapping("/api/user")
@Tag(name = "User", description = "User management")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  private static final Logger logger = Logger.getLogger(AdminController.class.getName());

  /**
   * Endpoint to fetch the user profile information.
   *
   * @return ResponseEntity containing user profile information or an error message.
   */
  @Operation(
      summary = "Get user profile",
      description = "Fetches the profile information of the currently authenticated user. "
          + "Returns user details such as email, name, role, and household location.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "User profile fetched successfully",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                        implementation = UserInfoResponse.class
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Error fetching user profile"
            )
        }
  )
  @GetMapping("/profile")
  public ResponseEntity<?> getUserProfile() {
    logger.info("Fetching user profile");
    try {
      UserInfoResponse userInfoResponse = userService.getUserInfo();
      return ResponseEntity.ok(userInfoResponse);
    } catch (Exception e) {
      logger.severe("Error fetching user profile: " + e.getMessage());
      return ResponseEntity.status(500).body("Error fetching user profile");
    }
  }
}

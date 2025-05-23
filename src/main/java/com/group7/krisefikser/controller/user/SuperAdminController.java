package com.group7.krisefikser.controller.user;

import com.group7.krisefikser.dto.request.user.InviteAdminRequest;
import com.group7.krisefikser.dto.response.user.AdminResponse;
import com.group7.krisefikser.service.user.SuperAdminService;
import com.group7.krisefikser.utils.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling requests related to super admin operations.
 * This class provides endpoints to invite admins,
 * retrieve all admins,
 * delete admins,
 * and send reset password emails to admins.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/super-admin")
public class SuperAdminController {

  private static final Logger logger = Logger.getLogger(SuperAdminController.class.getName());
  private final SuperAdminService superAdminService;

  /**
   * Endpoint to invite an admin.
   * This endpoint will accept a request containing the email of the admin to be invited.
   *
   * @param request The request containing the email of the admin to be invited.
   * @return ResponseEntity indicating the result of the operation.
   */
  @Operation(
      summary = "Invite an admin",
      description = "Sends an invitation to a new admin by email. "
          + "The system generates an invite token and sends it as a link to the specified email.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "InviteAdminRequest containing a valid email address",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = InviteAdminRequest.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Admin invited successfully",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Validation failed for the email format",
              content = @Content(mediaType = "application/json")
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @PostMapping("/invite")
  public ResponseEntity<Object> invite(@RequestBody @Valid InviteAdminRequest request,
                                       BindingResult bindingResult) {
    logger.info("Inviting admin request");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      superAdminService.inviteAdmin(request);
      logger.info("Admin invited successfully");
      return ResponseEntity.ok("Admin invited successfully");
    } catch (Exception e) {
      logger.severe("Error inviting admin: " + e.getMessage());
      return ResponseEntity.status(500).body("Error inviting admin");
    }
  }

  /**
   * Endpoint to retrieve all admins.
   * This endpoint fetches the list of all admins from the system.
   *
   * @return ResponseEntity containing the list of admins.
   */
  @Operation(
      summary = "Get all admins",
      description = "Retrieves a list of all admins currently registered in the system.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "List of admins retrieved successfully",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AdminResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error while retrieving admins",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @GetMapping("/admins")
  public ResponseEntity<List<AdminResponse>> getAllAdmins() {
    logger.info("Fetching all admins");
    try {
      List<AdminResponse> admins = superAdminService.getAllAdmins();
      logger.info("Fetched " + admins.size() + " admins successfully.");
      return new ResponseEntity<>(admins, HttpStatus.OK);
    } catch (Exception e) {
      logger.severe("Error fetching admins: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Endpoint to delete an admin.
   * This endpoint will accept the ID of the admin to be deleted.
   *
   * @param adminId The ID of the admin to be deleted.
   * @return ResponseEntity indicating the result of the operation.
   */
  @Operation(
      summary = "Delete an admin",
      description = "Deletes an admin based on the provided admin ID.",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "Admin deleted successfully"
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error while deleting admin",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @DeleteMapping("/delete/{adminId}")
  public ResponseEntity<Void> deleteAdmin(@PathVariable Long adminId) {
    logger.info("Trying to delete admin with ID: " + adminId);
    try {
      superAdminService.deleteAdmin(adminId);
      logger.info("Admin with ID " + adminId + " deleted successfully.");
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      logger.severe("Error deleting admin with ID " + adminId + ": " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Endpoint to send a new password link to an admin.
   * This endpoint will accept the email of the admin
   * to whom the new password link will be sent.
   *
   * @param email The email of the admin to whom the new password link will be sent.
   * @return ResponseEntity indicating the result of the operation.
   */
  @Operation(
      summary = "Send new password link to admin",
      description = "Sends a password reset link to the admin's email address.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "The email of the admin to send the reset link to",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = String.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Password reset link sent successfully",
              content = @Content(mediaType = "application/json")
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error while sending reset link",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @PostMapping("/admins/new-password-link")
  public ResponseEntity<Object> sendNewPasswordLink(@RequestBody String email) {
    logger.info("Trying to send new password link to: " + email);
    try {
      superAdminService.sendResetPasswordEmailToAdmin(email);
      logger.info("New password link sent to: " + email);
      return ResponseEntity.ok("New password link sent to: " + email);
    } catch (IllegalArgumentException e) {
      logger.warning("Invalid email format: " + e.getMessage());
      return ResponseEntity.badRequest().body("Invalid email format");
    } catch (Exception e) {
      logger.severe("Error sending new password link to " + email + ": " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}

package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.TwoFactorLoginRequest;
import com.group7.krisefikser.dto.request.UpdateRegisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.request.UpdateUnregisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.response.GetRegisteredPrivacyPolicyResponse;
import com.group7.krisefikser.dto.response.GetUnregisteredPrivacyPolicyResponse;
import com.group7.krisefikser.service.PrivacyPolicyService;
import com.group7.krisefikser.utils.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *  Controller class for handling requests related to privacy policy.
 *  This class provides endpoints to getting and updating privacy policy.
 */
@Controller
@RequestMapping("/api/privacy-policy")
@Tag(name = "Privacy Policy", description = "Privacy policy management")
@RequiredArgsConstructor
public class PrivacyPolicyController {

  private final PrivacyPolicyService privacyPolicyService;

  Logger logger = Logger.getLogger(PrivacyPolicyController.class.getName());

  /**
   * Endpoint to get the registered privacy policy.
   *
   * @return ResponseEntity containing the registered privacy policy.
   */
  @Operation(
      summary = "Get the registered privacy policy",
      description =
          "Fetches the currently registered privacy policy document for the user or system.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully retrieved the registered privacy policy",
              content =
              @Content(schema = @Schema(implementation = GetRegisteredPrivacyPolicyResponse.class))
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Server error while fetching the registered privacy policy",
              content = @Content(schema =
              @Schema(example = "Error fetching registered privacy policy"))
          )
      }
  )
  @GetMapping("/registered")
  public ResponseEntity<?> getRegisteredPrivacyPolicy() {
    logger.info("Fetching registered privacy policy");

    try {
      GetRegisteredPrivacyPolicyResponse response =
          privacyPolicyService.getRegisteredPrivacyPolicy();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.severe("Error fetching registered privacy policy: " + e.getMessage());
      return ResponseEntity.status(500).body("Error fetching registered privacy policy");
    }
  }

  /**
   * Endpoint to get the unregistered privacy policy.
   *
   * @return ResponseEntity containing the unregistered privacy policy.
   */
  @Operation(
      summary = "Get the unregistered privacy policy",
      description = "Retrieves the current privacy policy that has not yet been "
          + "registered or accepted by the user or system.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully retrieved the unregistered privacy policy",
              content = @Content(schema = @Schema(implementation =
                  GetUnregisteredPrivacyPolicyResponse.class))
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Server error while fetching the unregistered privacy policy",
              content = @Content(schema = @Schema(example =
                  "Error fetching unregistered privacy policy"))
          )
      }
  )
  @GetMapping("/unregistered")
  public ResponseEntity<?> getUnregisteredPrivacyPolicy() {
    logger.info("Fetching unregistered privacy policy");
    try {
      GetUnregisteredPrivacyPolicyResponse response =
          privacyPolicyService.getUnregisteredPrivacyPolicy();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.severe("Error fetching unregistered privacy policy: " + e.getMessage());
      return ResponseEntity.status(500).body("Error fetching unregistered privacy policy");
    }
  }

  /**
   * Endpoint to update the registered privacy policy.
   *
   * @param request the request body containing the updated privacy policy.
   * @param bindingResult the result of the validation.
   * @return ResponseEntity indicating the success or failure of the operation.
   */
  @Operation(
      summary = "Update the registered privacy policy",
      description = "Updates the currently registered privacy policy with "
          + "new content provided in the request body.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "UpdateUnregisteredPrivacyPolicyRequest containing privacy policy",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = TwoFactorLoginRequest.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Registered privacy policy updated successfully",
              content =
              @Content(schema = @Schema(example = "Registered privacy policy updated successfully"))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Validation error in request data",
              content = @Content(schema = @Schema(example = "Validation error details"))
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Server error while updating the registered privacy policy",
              content = @Content(schema =
              @Schema(example = "Error updating registered privacy policy"))
          )
      }
  )
  @PostMapping("/registered")
  public ResponseEntity<?> updateRegisteredPrivacyPolicy(
      @RequestBody @Valid UpdateRegisteredPrivacyPolicyRequest request,
      BindingResult bindingResult) {
    logger.info("Updating registered privacy policy");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      privacyPolicyService.updateRegisteredPrivacyPolicy(request);
      return ResponseEntity.ok("Registered privacy policy updated successfully");
    } catch (Exception e) {
      logger.severe("Error updating registered privacy policy: " + e.getMessage());
      return ResponseEntity.status(500).body("Error updating registered privacy policy");
    }
  }

  /**
   * Endpoint to update the unregistered privacy policy.
   *
   * @param request the request body containing the updated privacy policy.
   * @param bindingResult the result of the validation.
   * @return ResponseEntity indicating the success or failure of the operation.
   */
  @Operation(
      summary = "Update the unregistered privacy policy",
      description = "Updates the unregistered privacy policy content for users "
          + "who are not registered in the system.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "UpdateUnregisteredPrivacyPolicyRequest containing privacy policy",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = TwoFactorLoginRequest.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Unregistered privacy policy updated successfully",
              content = @Content(schema = @Schema(example =
                  "Unregistered privacy policy updated successfully"))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Validation error in request data",
              content = @Content(schema = @Schema(example = "Validation error details"))
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Server error while updating the unregistered privacy policy",
              content = @Content(schema =
              @Schema(example = "Error updating unregistered privacy policy"))
          )
      }
  )
  @PostMapping("/unregistered")
  public ResponseEntity<?> updateUnregisteredPrivacyPolicy(
      @RequestBody @Valid UpdateUnregisteredPrivacyPolicyRequest request,
      BindingResult bindingResult) {
    logger.info("Updating unregistered privacy policy");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      privacyPolicyService.updateUnregisteredPrivacyPolicy(request);
      return ResponseEntity.ok("Unregistered privacy policy updated successfully");
    } catch (Exception e) {
      logger.severe("Error updating unregistered privacy policy: " + e.getMessage());
      return ResponseEntity.status(500).body("Error updating unregistered privacy policy");
    }
  }
}

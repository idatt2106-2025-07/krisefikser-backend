package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.AddNonUserMemberRequest;
import com.group7.krisefikser.dto.request.DeleteNonUserMemberRequest;
import com.group7.krisefikser.dto.request.UpdateNonUserMemberRequest;
import com.group7.krisefikser.service.NonUserMemberService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for managing non-user members in a household.
 * This class provides endpoints for adding, updating, and deleting non-user members.
 */
@Controller
@RequestMapping("/api/non-user-member")
@Tag(name = "Non-User Member", description = "Non-user member management")
@RequiredArgsConstructor
public class NonMemberUserController {

  private final NonUserMemberService nonUserMemberService;

  private static final Logger logger = Logger.getLogger(NonUserMemberService.class.getName());


  /**
   * Adds a non-user member to a household.
   *
   * @param request the request object containing the details of the non-user member to be added
   * @return a response entity indicating the success or failure of the operation
   */
  @Operation(
      summary = "Add Non-User Member",
      description = "Adds a non-user member to the household.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "AddNonUserMemberRequest with non-user member details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = AddNonUserMemberRequest.class
                )
            )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Non-user member added successfully",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = String.class
                  )
              )
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "500",
              description = "Error adding non-user member"
          )
      }
  )
  @PostMapping("/add")
  public ResponseEntity<?> addNonUserMember(
      @Valid @RequestBody AddNonUserMemberRequest request, BindingResult bindingResult) {
    logger.info("Adding non-user member");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }

    try {
      nonUserMemberService.addNonUserMember(request);
      return ResponseEntity.ok("Non-user member added successfully");
    } catch (Exception e) {
      logger.severe("Error adding non-user member: " + e.getMessage());
      return ResponseEntity.status(500).body("Error adding non-user member");
    }
  }

  /**
   * Updates an existing non-user member in a household.
   *
   * @param request the request object containing the updated details of the non-user member
   * @return a response entity indicating the success or failure of the operation
   */
  @Operation(
      summary = "Update Non-User Member",
      description = "Updates an existing non-user member in the household.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "UpdateNonUserMemberRequest with updated non-user member details",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = UpdateNonUserMemberRequest.class
              )
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Non-user member updated successfully",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = String.class
                  )
              )
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "500",
              description = "Error updating non-user member"
          )
      }
  )
  @PostMapping("/update")
  public ResponseEntity<?> updateNonUserMember(
      @Valid @RequestBody UpdateNonUserMemberRequest request, BindingResult bindingResult) {
    logger.info("Updating non-user member");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }
    try {
      nonUserMemberService.updateNonUserMember(request);
      return ResponseEntity.ok("Non-user member updated successfully");
    } catch (Exception e) {
      logger.severe("Error updating non-user member: " + e.getMessage());
      return ResponseEntity.status(500).body("Error updating non-user member");
    }
  }

  /**
   * Deletes a non-user member from a household.
   *
   * @param request the request object containing the ID of the non-user member to be deleted
   * @return a response entity indicating the success or failure of the operation
   */
  @Operation(
      summary = "Delete Non-User Member",
      description = "Deletes a non-user member from the household.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "DeleteNonUserMemberRequest with non-user member ID",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DeleteNonUserMemberRequest.class
              )
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Non-user member deleted successfully",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = String.class
                  )
              )
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "500",
              description = "Error deleting non-user member"
          )
      }
  )
  @DeleteMapping("/delete")
  public ResponseEntity<?> deleteNonUserMember(
      @Valid @RequestBody DeleteNonUserMemberRequest request, BindingResult bindingResult) {
    logger.info("Deleting non-user member");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }
    try {
      nonUserMemberService.deleteNonUserMember(request);
      return ResponseEntity.ok("Non-user member deleted successfully");
    } catch (Exception e) {
      logger.severe("Error deleting non-user member: " + e.getMessage());
      return ResponseEntity.status(500).body("Error deleting non-user member");
    }
  }
}

package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.GeneralInfoRequest;
import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.model.GeneralInfo;
import com.group7.krisefikser.service.GeneralInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller class for handling general information requests.
 * This class provides endpoints for retrieving, adding, updating, and deleting general information.
 * It uses the GeneralInfoService to perform the operations.
 */
@RestController
@RequestMapping("/api/general-info")
public class GeneralInfoController {
  @Autowired
  private GeneralInfoService generalInfoService;

  /**
   * Endpoint to get all general information.
   * This method retrieves all general information from the system.
   * It returns a list of GeneralInfo objects.
   *
   * @return a list of GeneralInfo objects
   */
  @Operation(
      summary = "Get all general information",
      description = "Retrieves all general information entries visible to users.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully retrieved general information",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error",
              content = @Content(mediaType = "application/json"))
      }
  )
  @GetMapping("/all")
  public List<GeneralInfo> getAllGeneralInfo() {
    return generalInfoService.getAllGeneralInfo();
  }

  /**
   * Endpoint to get general information by theme.
   * This method retrieves general information based on the specified theme.
   * It accepts a theme as a path variable and returns a list of GeneralInfo objects.
   *
   * @param theme the theme to filter the general information
   * @return a list of GeneralInfo objects matching the specified theme
   */
  @Operation(summary = "Get general information by theme")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved general information",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = GeneralInfo.class))),
      @ApiResponse(responseCode = "400", description = "Invalid theme provided",
          content = @Content)
  })
  @GetMapping("/{theme}")
  public List<GeneralInfo> getGeneralInfoByTheme(@Valid @PathVariable String theme) {
    Theme parsedTheme;
    try {
      parsedTheme = Theme.valueOf(theme.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid theme: " + theme);
    }
    return generalInfoService.getGeneralInfoByTheme(parsedTheme);
  }

  /**
   * Endpoint to add general information.
   * This method adds a new GeneralInfo object to the system.
   * It accepts a GeneralInfo object in the request body.
   *
   * @param request the GeneralInfoRequest object containing the information to be added
   */
  @Operation(
      summary = "Add general information (admin only)",
      description = "Allows administrators to add new general information content "
          + "such as guidelines, announcements, etc.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "General information details",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = GeneralInfoRequest.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully added general information",
              content = @Content(mediaType = "application/json")
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "403",
              description = "Forbidden – user does not have admin privileges",
              content = @Content(mediaType = "application/json")
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @PostMapping("/admin/add")
  public void addGeneralInfo(@Valid @RequestBody GeneralInfoRequest request) {
    generalInfoService.addGeneralInfo(request);
  }

  /**
   * Endpoint to update general information.
   * This method updates an existing GeneralInfo object in the system.
   * It accepts a GeneralInfo object in the request body.
   *
   * @param request the GeneralInfoRequest object containing the updated information
   * @param id the ID of the GeneralInfo object to be updated
   */
  @Operation(
      summary = "Update general information (admin only)",
      description = "Updates the content of an existing general information item.",
      parameters = {
          @Parameter(name = "id", in = ParameterIn.PATH, required = true,
              description = "ID of the general information to update",
              schema = @Schema(type = "integer", format = "int64"))
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Updated general information content",
          required = true,
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = GeneralInfoRequest.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200", description =
              "Successfully updated general information"),
          @ApiResponse(responseCode = "400", description = "Invalid input data"),
          @ApiResponse(responseCode = "403", description = "Forbidden – insufficient privileges"),
          @ApiResponse(responseCode = "404", description = "General information not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  @PutMapping("/admin/update/{id}")
  public void updateGeneralInfo(@Valid @RequestBody GeneralInfoRequest request,
                                @PathVariable Long id) {
    generalInfoService.updateGeneralInfo(request, id);
  }

  /**
   * Endpoint to delete general information.
   * This method deletes a GeneralInfo object from the system based on its ID.
   * It accepts the ID of the GeneralInfo object as a path variable.
   *
   * @param id the ID of the GeneralInfo object to be deleted
   */
  @Operation(
      summary = "Delete general information (admin only)",
      description = "Deletes an existing general information item by its ID.",
      parameters = {
          @Parameter(name = "id", in = ParameterIn.PATH, required = true,
              description = "ID of the general information to delete",
              schema = @Schema(type = "integer", format = "int64"))
      },
      responses = {
          @ApiResponse(responseCode = "204", description =
              "Successfully deleted general information"),
          @ApiResponse(responseCode = "403", description = "Forbidden – insufficient privileges"),
          @ApiResponse(responseCode = "404", description = "General information not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  @DeleteMapping("/admin/delete/{id}")
  public void deleteGeneralInfo(@PathVariable Long id) {
    generalInfoService.deleteGeneralInfo(id);
  }
}

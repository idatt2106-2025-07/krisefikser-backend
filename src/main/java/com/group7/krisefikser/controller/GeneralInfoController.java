package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.GeneralInfoRequest;
import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.model.GeneralInfo;
import com.group7.krisefikser.service.GeneralInfoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
  @GetMapping("/{theme}")
  public List<GeneralInfo> getGeneralInfoByTheme(@PathVariable String theme) {
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
   * @param info the GeneralInfo object to be added
   */
  @PostMapping("/admin/add")
  public void addGeneralInfo(@RequestBody GeneralInfoRequest request) {
    generalInfoService.addGeneralInfo(request);
  }

  /**
   * Endpoint to update general information.
   * This method updates an existing GeneralInfo object in the system.
   * It accepts a GeneralInfo object in the request body.
   *
   * @param info the GeneralInfo object to be updated
   */
  @PutMapping("/admin/update")
  public void updateGeneralInfo(@RequestBody GeneralInfoRequest request) {
    generalInfoService.updateGeneralInfo(request);
  }

  /**
   * Endpoint to delete general information.
   * This method deletes a GeneralInfo object from the system based on its ID.
   * It accepts the ID of the GeneralInfo object as a path variable.
   *
   * @param id the ID of the GeneralInfo object to be deleted
   */
  @DeleteMapping("/admin/delete/{id}")
  public void deleteGeneralInfo(@PathVariable GeneralInfoRequest request) {
    generalInfoService.deleteGeneralInfo(request);
  }
}

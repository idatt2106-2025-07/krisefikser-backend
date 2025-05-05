package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.response.SuperAdminResponse;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.service.SuperAdminService;
import com.group7.krisefikser.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {

  private static final Logger logger = Logger.getLogger(SuperAdminController.class.getName());
  private final SuperAdminService superAdminService;

  public SuperAdminController(SuperAdminService superAdminService) {
    this.superAdminService = superAdminService;
  }

  @GetMapping("/admins")
  public ResponseEntity<List<User>> getAllAdmins() {
    return ResponseEntity.ok(superAdminService.getAllAdmins());
  }

  @DeleteMapping("/admins/{adminId}")
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

  @PostMapping("/admins/new-password-link")
  public ResponseEntity<String> sendNewPasswordLink(@RequestParam String email) {
    logger.info("Trying to send new password link to: " + email);
    try {
      superAdminService.sendResetPasswordEmailToAmdmin(email);
      logger.info("New password link sent to: " + email);
      return ResponseEntity.ok("New password link sent to: " + email);
    } catch (Exception e) {
      logger.severe("Error sending new password link to " + email + ": " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}

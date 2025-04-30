package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.InviteAdminRequest;
import com.group7.krisefikser.service.AdminService;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  private static final Logger logger = Logger.getLogger(AdminController.class.getName());

  @PostMapping("/invite")
  public ResponseEntity<String> invite(@RequestBody InviteAdminRequest request) {
    logger.info("Inviting admin");
    try {
      adminService.inviteAdmin(request);
      logger.info("Admin invited successfully");
      return ResponseEntity.ok("Admin invited successfully");
    } catch (Exception e) {
      logger.severe("Error inviting admin: " + e.getMessage());
      return ResponseEntity.status(500).body("Error inviting admin");
    }
  }

  /**
   * Endpoint to register an admin.
   * This endpoint will accept a request containing the invite token and other registration details.
   *
   * @param request The request containing the invite token and other registration details.
   * @return ResponseEntity indicating the result of the operation.
   */
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterAdminRequest request) {
    logger.info("Registering admin request");
    try {
      adminService.registerAdmin(request);
      logger.info("Admin registered successfully");
      return ResponseEntity.ok("Admin registered successfully");
    } catch (Exception e) {
      logger.severe("Error registering admin: " + e.getMessage());
      return ResponseEntity.status(500).body("Error registering admin");
    }
  }
}

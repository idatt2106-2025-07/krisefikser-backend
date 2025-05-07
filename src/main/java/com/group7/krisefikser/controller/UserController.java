package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.response.UserInfoResponse;
import com.group7.krisefikser.service.UserService;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  private static final Logger logger = Logger.getLogger(AdminController.class.getName());

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

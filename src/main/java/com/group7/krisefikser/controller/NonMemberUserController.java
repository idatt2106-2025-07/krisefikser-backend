package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.AddNonUserMemberRequest;
import com.group7.krisefikser.dto.request.DeleteNonUserMemberRequest;
import com.group7.krisefikser.dto.request.UpdateNonUserMemberRequest;
import com.group7.krisefikser.service.NonUserMemberService;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/non-user-member")
@RequiredArgsConstructor
public class NonMemberUserController {

  private final NonUserMemberService nonUserMemberService;

  private static final Logger logger = Logger.getLogger(NonUserMemberService.class.getName());


  @PostMapping("/add")
  public ResponseEntity<?> addNonUserMember(@RequestBody AddNonUserMemberRequest request) {
    logger.info("Adding non-user member");
    try {
      nonUserMemberService.addNonUserMember(request);
      return ResponseEntity.ok("Non-user member added successfully");
    } catch (Exception e) {
      logger.severe("Error adding non-user member: " + e.getMessage());
      return ResponseEntity.status(500).body("Error adding non-user member");
    }
  }

  @PostMapping("/update")
  public ResponseEntity<?> updateNonUserMember(@RequestBody UpdateNonUserMemberRequest request) {
    logger.info("Updating non-user member");

    try {
      nonUserMemberService.updateNonUserMember(request);
      return ResponseEntity.ok("Non-user member updated successfully");
    } catch (Exception e) {
      logger.severe("Error updating non-user member: " + e.getMessage());
      return ResponseEntity.status(500).body("Error updating non-user member");
    }
  }

  @DeleteMapping("/delete")
  public ResponseEntity<?> deleteNonUserMember(@RequestBody DeleteNonUserMemberRequest request) {
    logger.info("Deleting non-user member");

    try {
      nonUserMemberService.deleteNonUserMember(request);
      return ResponseEntity.ok("Non-user member deleted successfully");
    } catch (Exception e) {
      logger.severe("Error deleting non-user member: " + e.getMessage());
      return ResponseEntity.status(500).body("Error deleting non-user member");
    }
  }
}

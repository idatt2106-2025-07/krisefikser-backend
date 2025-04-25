package com.group7.krisefikser.service;

import com.group7.krisefikser.enums.EmailTemplateType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailTemplateService {

  String getSubject(EmailTemplateType type) {
    return switch (type) {
      case PASSWORD_RESET -> "Password Reset Request";
      case HOUSEHOLD_INVITE -> "Household Invite";
      case ADMIN_INVITE -> "Admin Invite";
      case ADMIN_VERIFICATION -> "Admin Verification";
    };
  }

  String getBody(EmailTemplateType type, Map<String, String> params) {
    return switch (type) {
      case PASSWORD_RESET -> "Click the link below to reset your password:\n"
          + params.get("resetLink");
      case HOUSEHOLD_INVITE -> "You have been invited to join a household. Click the link below to accept the invitation:\n"
          + params.get("inviteLink");
      case ADMIN_INVITE -> "You have been invited to become an admin. Click the link below to accept the invitation:\n"
          + params.get("inviteLink");
      case ADMIN_VERIFICATION -> "Your admin account has been verified. You can now log in.\n"
          + params.get("loginLink");
    };
  }
}

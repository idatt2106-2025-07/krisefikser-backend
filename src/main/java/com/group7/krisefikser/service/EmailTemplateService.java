package com.group7.krisefikser.service;

import com.group7.krisefikser.enums.EmailTemplateType;
import java.util.Map;
import org.springframework.stereotype.Service;


/**
 * Service class for generating email templates.
 * This class provides methods to get the subject and body of different email templates.
 */
@Service
public class EmailTemplateService {

  /**
   * Gets the subject of the email template based on the type.
   *
   * @param type The type of the email template.
   * @return The subject of the email template.
   */
  String getSubject(EmailTemplateType type) {
    return switch (type) {
      case PASSWORD_RESET -> "Password Reset Request";
      case HOUSEHOLD_INVITE -> "Household Invite";
      case ADMIN_INVITE -> "Admin Invite";
      case ADMIN_VERIFICATION -> "Admin Verification";
    };
  }

  /**
   * Gets the body of the email template based on the type and parameters.
   *
   * @param type   The type of the email template.
   * @param params The parameters to be used in the template.
   * @return The body of the email template.
   */
  String getBody(EmailTemplateType type, Map<String, String> params) {
    return switch (type) {
      case PASSWORD_RESET -> "Click the link below to reset your password:\n"
          + params.get("resetLink");
      case HOUSEHOLD_INVITE -> "You have been invited to join a household.\n"
                              + "Click the link below to accept the invitation:\n"
          + params.get("inviteLink");
      case ADMIN_INVITE -> "You have been invited to become an admin.\n"
                            + "Click the link below to accept the invitation:\n"
          + params.get("inviteLink");
      case ADMIN_VERIFICATION -> "Your admin account has been verified. You can now log in.\n"
          + params.get("loginLink");
    };
  }
}

package com.group7.krisefikser.dto.request;

import lombok.Data;

/**
 * Request DTO for inviting an admin.
 * This class contains the email of the admin to be invited.
 */
@Data
public class InviteAdminRequest {
  private String email;
}

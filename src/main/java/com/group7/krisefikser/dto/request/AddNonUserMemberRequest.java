package com.group7.krisefikser.dto.request;

import com.group7.krisefikser.enums.NonUserMemberType;
import lombok.Data;

/**
 * Request DTO for adding a non-user member to a household.
 * This class contains the necessary information to create a new non-user member.
 */
@Data
public class AddNonUserMemberRequest {
  private String name;
  private NonUserMemberType type;
}

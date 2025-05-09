package com.group7.krisefikser.dto.request.household;

import com.group7.krisefikser.enums.NonUserMemberType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for adding a non-user member to a household.
 * This class contains the necessary information to create a new non-user member.
 */
@Data
public class AddNonUserMemberRequest {
  @NotBlank(message = "Name cannot be blank")
  private String name;
  private NonUserMemberType type;
}

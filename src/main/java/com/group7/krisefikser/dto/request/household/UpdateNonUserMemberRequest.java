package com.group7.krisefikser.dto.request.household;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Request DTO for deleting a non-user member from a household.
 * This class contains the ID of the non-user member to be deleted.
 */
@Data
public class UpdateNonUserMemberRequest {
  @NotBlank(message = "Name cannot be blank")
  private String name;
  private String type;
  @Positive(message = "Household ID must be positive")
  private long id;
}

package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for creating or updating an emergency group.
 * This class contains fields for the name and creation date of the group.
 * It is used to validate the input data for creating or updating an emergency group.
 */
@Data
@NoArgsConstructor
public class EmergencyGroupRequest {
  @NotNull(message = "Name cannot be null")
  @NotBlank(message = "Name cannot be blank")
  private String name;

  @Pattern(regexp = "^(\\d{4}-\\d{2}-\\d{2}.*)?$",
          message = "Date must be on yyyy-MM-dd format")
  private String createdAt;
}

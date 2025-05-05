package com.group7.krisefikser.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request to invite a user to join a household.
 * This model is used to store and transfer data related to invitation requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationRequest {
  @NotNull
  private Long householdId;

  @NotNull
  private Long invitedByUserId;

  @NotBlank
  @Email
  private String email;
}
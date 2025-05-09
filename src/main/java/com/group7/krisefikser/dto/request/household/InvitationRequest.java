package com.group7.krisefikser.dto.request.household;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

  @NotBlank
  @Email
  private String email;
}
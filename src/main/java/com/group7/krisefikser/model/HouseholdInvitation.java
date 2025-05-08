package com.group7.krisefikser.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an invitation for a user to join a household.
 * This model is used to store and transfer data related to email invitations.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdInvitation {
  private Long id;
  private Long householdId;
  private Long invitedByUserId;
  private String invitedEmail;
  private String invitationToken;
  private LocalDateTime createdAt;
}
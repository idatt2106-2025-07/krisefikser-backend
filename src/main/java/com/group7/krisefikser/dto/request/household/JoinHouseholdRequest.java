package com.group7.krisefikser.dto.request.household;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request for a user to join a household.
 * This model is used to store and transfer data related to join requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinHouseholdRequest {
  private Long id;
  private Long householdId;
  private Long userId;
}

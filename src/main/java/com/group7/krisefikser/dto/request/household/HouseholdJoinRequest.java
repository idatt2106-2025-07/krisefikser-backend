package com.group7.krisefikser.dto.request.household;

import lombok.Data;

/**
 * Represents a request to join a household.
 * This class contains the ID of the household that the user wants to join.
 * It is used to transfer data related to household join requests.
 */
@Data
public class HouseholdJoinRequest {
  private Long householdId;
}
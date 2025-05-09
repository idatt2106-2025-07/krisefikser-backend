package com.group7.krisefikser.dto.response.household;

import lombok.Data;

/**
 * Represents a response for a join household request.
 * This class contains the ID of the request, user ID, and household ID.
 * It is used to transfer data related to join household requests.
 */
@Data
public class JoinHouseholdRequestResponse {
  private Long id;
  private Long userId;
  private Long householdId;
}
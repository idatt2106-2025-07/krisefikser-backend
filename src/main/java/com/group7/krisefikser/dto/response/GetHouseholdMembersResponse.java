package com.group7.krisefikser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO for household members.
 * This class contains the necessary information to represent a household member.
 */
@Data
@AllArgsConstructor
public class GetHouseholdMembersResponse {
  private long id;
  private String name;
  private String type;
}

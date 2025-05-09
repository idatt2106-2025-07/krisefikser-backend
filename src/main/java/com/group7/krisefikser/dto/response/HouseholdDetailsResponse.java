package com.group7.krisefikser.dto.response;

import java.util.List;
import lombok.Data;

/**
 * Response DTO for household details.
 * Contains information about the household, its members, and non-user members.
 */
@Data
public class HouseholdDetailsResponse {
  private Long id;
  private String name;
  private double longitude;
  private double latitude;
  private List<HouseholdMemberResponse> members;
  private List<NonUserMemberResponse> nonUserMembers;
}


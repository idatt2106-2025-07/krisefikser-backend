package com.group7.krisefikser.dto.response;

import lombok.Data;

/**
 * DTO for group member position response.
 * Contains fields for latitude, longitude, and name.
 */
@Data
public class GroupMemberPositionResponse {
  private double latitude;
  private double longitude;
  private String name;
}

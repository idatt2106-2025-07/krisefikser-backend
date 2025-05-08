package com.group7.krisefikser.dto.response;

import com.group7.krisefikser.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO class representing the response for user information.
 * Contains user details such as email, name, role, and household location.
 */
@Data
@AllArgsConstructor
public class UserInfoResponse {
  private String email;
  private String name;
  private Role role;
  private double householdLatitude;
  private double householdLongitude;
  private boolean isSharingLocation;
}

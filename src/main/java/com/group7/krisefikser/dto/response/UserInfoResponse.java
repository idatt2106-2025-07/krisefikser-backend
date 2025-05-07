package com.group7.krisefikser.dto.response;

import com.group7.krisefikser.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {
  private String email;
  private String name;
  private Role role;
  private double householdLatitude;
  private double householdLongitude;
}

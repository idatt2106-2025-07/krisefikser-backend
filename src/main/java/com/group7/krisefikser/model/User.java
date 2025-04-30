package com.group7.krisefikser.model;

import com.group7.krisefikser.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
  private Long id;
  private String email;
  private String name;
  private Long householdId;
  private String password;
  private Role role;
}

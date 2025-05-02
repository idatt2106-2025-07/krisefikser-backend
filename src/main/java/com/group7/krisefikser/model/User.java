package com.group7.krisefikser.model;

import com.group7.krisefikser.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a user in the system.
 * It contains the user's ID, email, name, password, household ID, and role.
 * This class is used to create, update, and retrieve user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private Long id;
  private String email;
  private String name;
  private Long householdId;
  private String password;
  private Role role;
  private Boolean verified;
}

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

  /**
   * Constructor for User class. This constructor is used to create a new user object.
   *
   * @param email    the email of the user
   * @param name     the name of the user
   * @param password the password of the user
   * @param role     the role of the user
   */
  public User(String email, String name, String password, Long householdId, Role role) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.householdId = householdId;
    this.role = role;
  }

  /**
   * Constructor for User class. This constructor is used to create a new user object
   * without a role.
   *
   * @param email   the email of the user
   * @param name    the name of the user
   * @param hashedPassword the hashed password of the user
   * @param householdId the household ID of the user
   */
  public User(String email, String name, String hashedPassword, Long householdId) {
    this.name = name;
    this.email = email;
    this.password = hashedPassword;
    this.householdId = householdId;
  }

  /**
   * Constructor for User class. This constructor is used to create a new user object
   * without a household ID and role.
   *
   * @param email  the email of the user
   * @param name   the name of the user
   * @param hashedPassword the hashed password of the user
   */
  public User(String email, String name, String hashedPassword) {
    this.name = name;
    this.email = email;
    this.password = hashedPassword;
  }
}


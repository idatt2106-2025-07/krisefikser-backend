package com.group7.krisefikser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  public User(String email, String name, String password, Role role) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.role = role;
  }
}


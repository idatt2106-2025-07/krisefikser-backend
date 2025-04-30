package com.group7.krisefikser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user entity in the system.
 * This class contains user details such as email, name, household association, and role.
 * Annotations:
 * - @Data: Generates getters, setters, toString, equals, and hashCode methods.
 * - @AllArgsConstructor: Generates a constructor with all fields as parameters.
 * - @NoArgsConstructor: Generates a no-argument constructor.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinHouseholdRequest {
  private Long id;
  private Long householdId;
  private Long userId;
}

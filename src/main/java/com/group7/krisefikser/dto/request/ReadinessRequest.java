package com.group7.krisefikser.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * This class represents a request to check the readiness of a household for an emergency.
 * It contains the household ID, emergency group ID, total calories available,
 * and the daily calorie requirement.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadinessRequest {
  private Long householdId;
  private Long emergencyGroupId;
  private int totalCalories;
  private double dailyCalorieRequirement;
}

package com.group7.krisefikser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinHouseholdRequest {
  private Long id;
  private Long householdId;
  private Long userId;
}

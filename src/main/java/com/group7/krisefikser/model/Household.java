package com.group7.krisefikser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Household {
  private Long id;
  private String name;
  private Double longitude;
  private Double latitude;
}

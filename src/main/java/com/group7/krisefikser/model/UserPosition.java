package com.group7.krisefikser.model;

import lombok.Data;

@Data
public class UserPosition {
  private String latitude;
  private String longitude;
  private String name;
  private Long userId;
}

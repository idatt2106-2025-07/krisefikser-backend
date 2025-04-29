package com.group7.krisefikser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AuthResponse {
  private String email;
  private String message;
  private Date expiryDate;
  private Long id;
}

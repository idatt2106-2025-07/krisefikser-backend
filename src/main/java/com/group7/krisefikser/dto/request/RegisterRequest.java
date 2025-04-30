package com.group7.krisefikser.dto.request;

import com.group7.krisefikser.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
  private String name;
  private String email;
  private String password;
}

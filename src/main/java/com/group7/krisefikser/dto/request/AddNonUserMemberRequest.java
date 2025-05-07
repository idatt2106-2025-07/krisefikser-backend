package com.group7.krisefikser.dto.request;

import com.group7.krisefikser.enums.NonUserMemberType;
import lombok.Data;

@Data
public class AddNonUserMemberRequest {
  private String name;
  private NonUserMemberType type;
}

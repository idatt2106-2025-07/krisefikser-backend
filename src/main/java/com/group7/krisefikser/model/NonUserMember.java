package com.group7.krisefikser.model;

import com.group7.krisefikser.enums.NonUserMemberType;
import lombok.Data;

@Data
public class NonUserMember {
  private long id;
  private String name;
  private NonUserMemberType type;
  private long householdId;
}

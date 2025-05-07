package com.group7.krisefikser.dto.request;

import lombok.Data;

@Data
public class UpdateNonUserMemberRequest {
  private String name;
  private String type;
  private long id;
}

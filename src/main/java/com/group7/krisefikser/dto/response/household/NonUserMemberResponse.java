package com.group7.krisefikser.dto.response.household;

import lombok.Data;

/**
 * Represents a response containing information about a household member.
 * This class contains the ID, name, and email of the household member.
 * It is used to transfer data related to household member responses.
 */
@Data
public class NonUserMemberResponse {
  private Long id;
  private String name;
  private String type;
}

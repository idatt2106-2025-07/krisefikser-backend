package com.group7.krisefikser.dto.response.household;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Response class representing an emergency group.
 * This class contains fields for the ID, name, and creation date of the group.
 * It is used to map the data from the database to a Java object for response purposes.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class EmergencyGroupResponse {
  private Long id;
  private String name;
  private String createdAt;
}

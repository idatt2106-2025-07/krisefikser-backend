package com.group7.krisefikser.model;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing an emergency group.
 * This class contains fields for the ID, name, and creation date of the group.
 * It is used to map the data from the database to a Java object.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyGroup {
  private Long id;
  private String name;
  private Date createdAt;
}

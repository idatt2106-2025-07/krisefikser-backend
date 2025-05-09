package com.group7.krisefikser.model.household;

import com.group7.krisefikser.enums.NonUserMemberType;
import lombok.Data;

/**
 * Represents a non-user member of a household.
 * This class is used to store information about non-user members
 * such as children, pets, or other entities associated with a household.
 */
@Data
public class NonUserMember {
  private long id;
  private String name;
  private NonUserMemberType type;
  private long householdId;
}

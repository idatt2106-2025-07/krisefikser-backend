package com.group7.krisefikser.model.household;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an invitation to join an emergency group.
 * This class contains the ID of the invitation, the ID of the household,
 * the ID of the group, and the timestamp when the invitation was created.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyGroupInvitation {
  private Long id;
  private Long householdId;
  private Long groupId;
  private LocalDateTime createdAt;
}

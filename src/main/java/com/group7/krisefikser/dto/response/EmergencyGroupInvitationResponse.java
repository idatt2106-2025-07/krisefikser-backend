package com.group7.krisefikser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a response containing information about an emergency group invitation.
 * This class contains the ID of the invitation, the ID of the household,
 * the ID of the group, the timestamp when the invitation was created,
 * and the name of the group.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyGroupInvitationResponse {
  private Long id;
  private Long householdId;
  private Long groupId;
  private String createdAt;
  private String groupName;
}

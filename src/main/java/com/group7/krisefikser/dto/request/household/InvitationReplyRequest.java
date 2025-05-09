package com.group7.krisefikser.dto.request.household;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request to reply to an invitation.
 * This class contains a boolean field indicating whether the invitation is accepted or not.
 * It is used to transfer data related to invitation replies.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationReplyRequest {
  @NotNull
  Boolean isAccept;
}

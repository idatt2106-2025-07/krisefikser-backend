package com.group7.krisefikser.dto.request;

import lombok.Data;

/**
 * Request DTO for deleting a non-user member from a household.
 * This class contains the ID of the non-user member to be deleted.
 */
@Data
public class DeleteNonUserMemberRequest {
  private long id;
}

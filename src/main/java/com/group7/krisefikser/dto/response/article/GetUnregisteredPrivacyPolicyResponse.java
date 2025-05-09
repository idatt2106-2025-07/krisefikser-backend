package com.group7.krisefikser.dto.response.article;

import lombok.Data;

/**
 * Response class for getting the registered privacy policy.
 * This class contains a single field for the registered privacy policy.
 */
@Data
public class GetUnregisteredPrivacyPolicyResponse {
  private String unregistered;
}

package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for sorting storage items.
 * This class can be used to encapsulate any parameters needed for the request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageItemSortRequest {
  @Pattern(regexp = "(?i)(quantity|expirationDate|name)",
      message = "Sort by must be either 'quantity' or 'expirationDate', or 'name'")
  private String sortBy = "expirationDate";

  @Pattern(regexp = "(?i)(asc|desc)", message = "Sort direction must be either 'asc' or 'desc'")
  private String sortDirection = "asc";
}
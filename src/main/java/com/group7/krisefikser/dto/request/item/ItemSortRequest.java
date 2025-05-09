package com.group7.krisefikser.dto.request.item;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for sorting items.
 * This class can be used to encapsulate any parameters needed for the request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemSortRequest {
  @Pattern(regexp = "(?i)(name|calories)", message = "Sort by must be either 'name' or 'calories'")
  private String sortBy = "name";

  @Pattern(regexp = "(?i)(asc|desc)", message = "Sort direction must be either 'asc' or 'desc'")
  private String sortDirection = "asc";
}
package com.group7.krisefikser.dto.request;

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
  private String sortBy = "name";
  private String sortDirection = "asc";
}
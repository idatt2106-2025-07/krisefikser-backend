package com.group7.krisefikser.dto.request.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for filtering storage items by household ID.
 * This class can be used to encapsulate any parameters needed for the request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageItemFilterRequest {
  private Integer householdId;
}
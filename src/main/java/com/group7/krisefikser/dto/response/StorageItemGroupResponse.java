package com.group7.krisefikser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO class for a storage item response in a group.
 * This class is used to add to the response data for a storage item
 * response and add householdName.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageItemGroupResponse {
  private StorageItemResponse storageItem;
  private String householdName;
}

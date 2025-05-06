package com.group7.krisefikser.dto.response;

import com.group7.krisefikser.model.StorageItem;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Response class for storage item details.
 * This class can be used to encapsulate the response data for a storage item.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageItemResponse {
  private int id;
  private LocalDateTime expirationDate;
  private int quantity;
  private int householdId;
  private int itemId;
  private boolean isShared;
  private ItemResponse item;

  /**
   * Converts a StorageItem entity to a StorageItemResponse.
   *
   * @param storageItem the StorageItem entity
   * @return the StorageItemResponse
   */
  public static StorageItemResponse fromEntity(StorageItem storageItem) {
    return new StorageItemResponse(
            storageItem.getId(),
            storageItem.getExpirationDate(),
            storageItem.getQuantity(),
            storageItem.getHouseholdId(),
            storageItem.getItemId(),
            storageItem.isShared(),
            null
    );
  }

  /**
   * Converts a StorageItem entity to a StorageItemResponse with item details.
   *
   * @param storageItem  the StorageItem entity
   * @param itemResponse the ItemResponse for the associated item
   * @return the StorageItemResponse with item details
   */
  public static StorageItemResponse fromEntityWithItem(StorageItem storageItem,
                                                       ItemResponse itemResponse) {
    return new StorageItemResponse(
            storageItem.getId(),
            storageItem.getExpirationDate(),
            storageItem.getQuantity(),
            storageItem.getHouseholdId(),
            storageItem.getItemId(),
            storageItem.isShared(),
            itemResponse
    );
  }
}
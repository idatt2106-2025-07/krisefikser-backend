package com.group7.krisefikser.dto.request;

import com.group7.krisefikser.model.StorageItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * Request class for creating or updating a storage item.
 * This class can be used to encapsulate any parameters needed for the request.
 * It also includes validation constraints for the storage item attributes.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageItemRequest {
  @NotNull(message = "Expiration date cannot be null")
  private LocalDateTime expirationDate;

  @Min(value = 0, message = "Quantity cannot be negative")
  private int quantity;

  @Min(value = 1, message = "Item ID must be positive")
  private int itemId;

  /**
   * Converts this request to a StorageItem entity.
   * The household ID is not set here, it will be set from the path variable in the controller.
   *
   * @return the StorageItem entity
   */
  public StorageItem toEntity() {
    StorageItem storageItem = new StorageItem();
    storageItem.setExpirationDate(expirationDate);
    storageItem.setQuantity(quantity);
    storageItem.setItemId(itemId);
    return storageItem;
  }
}
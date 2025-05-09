package com.group7.krisefikser.dto.request.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for changing the shared status of a storage item.
 * This class contains the shared status and quantity of the item.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStorageItemSharedStatusRequest {
  @NotNull
  private Boolean isShared;
  @NotNull
  @Min(value = 0, message = "Quantity cannot be negative")
  private Double quantity;
}

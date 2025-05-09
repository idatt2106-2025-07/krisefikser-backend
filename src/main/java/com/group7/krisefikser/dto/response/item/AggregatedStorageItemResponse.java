package com.group7.krisefikser.dto.response.item;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Response class for aggregated storage item details.
 * This combines multiple storage items of the same item type,
 * summing their quantities and finding the earliest expiration date.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedStorageItemResponse {
  private int itemId;
  private ItemResponse item;
  private double totalQuantity;
  private LocalDateTime earliestExpirationDate;
}
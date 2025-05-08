package com.group7.krisefikser.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a storage item with properties such as expiration date, quantity,
 * and references to household and item.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageItem {
  private int id;
  private LocalDateTime expirationDate;
  private double quantity;
  private int householdId;
  private int itemId;
  private boolean isShared;
}
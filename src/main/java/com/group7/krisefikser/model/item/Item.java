package com.group7.krisefikser.model.item;

import com.group7.krisefikser.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an item with its properties such as name, unit, calories, and type.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
  private int id;
  private String name;
  private String unit;
  private int calories;
  private ItemType type;
}
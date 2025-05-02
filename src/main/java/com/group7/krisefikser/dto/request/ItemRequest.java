package com.group7.krisefikser.dto.request;

import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for creating or updating an item.
 * This class can be used to encapsulate any parameters needed for the request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
  private String name;
  private String unit;
  private int calories;
  private ItemType type;

  /**
   * Converts this request to an Item entity.
   *
   * @return the Item entity
   */
  public Item toEntity() {
    Item item = new Item();
    item.setName(name);
    item.setUnit(unit);
    item.setCalories(calories);
    item.setType(type);
    return item;
  }

  /**
   * Converts this request to an Item entity with the specified ID.
   *
   * @param id the ID to set for the Item entity
   * @return the Item entity with the specified ID
   */
  public Item toEntity(int id) {
    Item item = toEntity();
    item.setId(id);
    return item;
  }
}
package com.group7.krisefikser.dto.request;

import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.Item;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for creating or updating an item.
 * This class can be used to encapsulate any parameters needed for the request.
 * It also includes validation constraints for the item attributes.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
  @NotBlank(message = "Item name cannot be empty")
  private String name;

  @NotBlank(message = "Item unit cannot be empty")
  private String unit;

  @Min(value = 0, message = "Calories cannot be negative")
  private int calories;

  @NotNull(message = "Item type must be specified")
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
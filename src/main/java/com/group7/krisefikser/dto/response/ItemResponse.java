package com.group7.krisefikser.dto.response;

import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response class for item details.
 * This class can be used to encapsulate the response data for an item.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponse {
  private int id;
  private String name;
  private String unit;
  private int calories;
  private ItemType type;

  /**
   * Converts an Item entity to an ItemResponse.
   *
   * @param item the Item entity
   * @return the ItemResponse
   */
  public static ItemResponse fromEntity(Item item) {
    return new ItemResponse(
      item.getId(),
      item.getName(),
      item.getUnit(),
      item.getCalories(),
      item.getType()
    );
  }
}
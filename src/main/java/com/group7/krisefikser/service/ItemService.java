package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.ItemRequest;
import com.group7.krisefikser.dto.response.ItemResponse;
import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.Item;
import com.group7.krisefikser.repository.ItemRepo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * This class is a service for managing items.
 * It provides methods to get, add, update, delete, sort and filter items.
 * It uses ItemRepo to interact with the database.
 */
@Service
@RequiredArgsConstructor
public class ItemService {
  private final ItemRepo itemRepo;

  /**
   * Retrieves all items from the repository.
   *
   * @return A list of all items available in the database.
   */
  public List<Item> getAllItems() {
    return itemRepo.getAllItems();
  }

  /**
   * Retrieves an item by its ID.
   *
   * @param id The ID of the item to retrieve.
   * @return The item with the specified ID.
   */
  public Item getItemById(int id) {
    return itemRepo.findById(id)
      .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
  }

  /**
   * Retrieves items by their types.
   *
   * @param types A list of item types to filter by.
   * @return A list of items that match the specified types.
   */
  public List<Item> getItemsByTypes(List<ItemType> types) {
    if (types == null || types.isEmpty()) {
      return getAllItems();
    }

    return getAllItems().stream()
      .filter(item -> types.contains(item.getType()))
      .collect(Collectors.toList());
  }

  /**
   * Creates a comparator based on the specified sort criteria.
   *
   * @param sortBy        The field to sort by (e.g., "calories" or "name").
   * @param sortDirection The direction of sorting (e.g., "asc" or "desc").
   * @return A comparator for sorting items.
   */
  private Comparator<Item> createComparator(String sortBy, String sortDirection) {
    Comparator<Item> comparator;
    if ("calories".equalsIgnoreCase(sortBy)) {
      comparator = Comparator.comparing(Item::getCalories);
    } else {
      comparator = Comparator.comparing(Item::getName, String.CASE_INSENSITIVE_ORDER);
    }

    return "desc".equalsIgnoreCase(sortDirection) ? comparator.reversed() : comparator;
  }

  /**
   * Retrieves all items sorted by the specified criteria.
   *
   * @param sortBy        The field to sort by (e.g., "calories" or "name").
   * @param sortDirection The direction of sorting (e.g., "asc" or "desc").
   * @return A list of sorted items.
   */
  public List<Item> getSortedItems(String sortBy, String sortDirection) {
    List<Item> items = getAllItems();
    Comparator<Item> comparator = createComparator(sortBy, sortDirection);

    return items.stream()
      .sorted(comparator)
      .collect(Collectors.toList());
  }

  /**
   * Retrieves items filtered by types and sorted by the specified criteria.
   *
   * @param types         A list of item types to filter by.
   * @param sortBy        The field to sort by (e.g., "calories" or "name").
   * @param sortDirection The direction of sorting (e.g., "asc" or "desc").
   * @return A list of filtered and sorted items.
   */
  public List<Item> getFilteredAndSortedItems(List<ItemType> types,
                                              String sortBy, String sortDirection) {
    // Apply filtering if types are provided
    List<Item> filteredItems = (types != null && !types.isEmpty())
        ? getItemsByTypes(types)
        : getAllItems();

    Comparator<Item> comparator = createComparator(sortBy, sortDirection);

    return filteredItems.stream()
      .sorted(comparator)
      .collect(Collectors.toList());
  }

  /**
   * Searches for items that match the given search term by name.
   * The search is case-insensitive and matches partial item names.
   *
   * @param searchTerm The term to search for in item names
   * @return A list of items that match the search term
   */
  public List<Item> searchItemsByName(String searchTerm) {
    if (searchTerm == null || searchTerm.trim().isEmpty()) {
      return getAllItems();
    }

    String lowercaseSearchTerm = searchTerm.toLowerCase().trim();

    return getAllItems().stream()
      .filter(item -> item.getName().toLowerCase().contains(lowercaseSearchTerm))
      .collect(Collectors.toList());
  }

  /**
   * Adds a new item to the repository after validating it.
   * This method ensures that the item meets all validation criteria
   * before delegating the addition to the repository.
   *
   * @param item The item to be added. It must contain valid name, unit, calories, and type.
   * @return The added item with its generated ID.
   */
  public Item addItem(Item item) {
    validateItem(item);
    return itemRepo.add(item);
  }

  /**
   * Updates an existing item in the repository after validating it.
   * This method ensures that the item meets all validation criteria
   * before delegating the update to the repository.
   *
   * @param id   The ID of the item to be updated.
   * @param item The updated item details. It must contain valid name, unit, calories, and type.
   * @return The updated item.
   */
  public Item updateItem(int id, Item item) {
    if (!itemExists(id)) {
      throw new RuntimeException("Item not found with id: " + id);
    }

    validateItem(item);
    item.setId(id);
    return itemRepo.update(item);
  }

  /**
   * Deletes an item from the repository by its ID.
   *
   * @param id The ID of the item to be deleted.
   */
  public void deleteItem(int id) {
    if (!itemExists(id)) {
      throw new RuntimeException("Item not found with id: " + id);
    }

    itemRepo.deleteById(id);
  }

  /**
   * Validates the item properties before adding or updating it.
   * This method checks if the name, unit, calories, and type are valid.
   *
   * @param item The item to be validated.
   */
  private void validateItem(Item item) {
    // Name validation
    if (item.getName() == null || item.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Item name cannot be empty");
    }

    // Unit validation
    if (item.getUnit() == null || item.getUnit().trim().isEmpty()) {
      throw new IllegalArgumentException("Item unit cannot be empty");
    }

    // Calories validation
    if (item.getCalories() < 0) {
      throw new IllegalArgumentException("Calories cannot be negative");
    }

    // Type validation
    if (item.getType() == null) {
      throw new IllegalArgumentException("Item type must be specified");
    }
  }

  /**
   * Checks if an item exists in the repository by its ID.
   *
   * @param id The ID of the item to check.
   * @return true if the item exists, false otherwise.
   */
  public boolean itemExists(int id) {
    return itemRepo.findById(id).isPresent();
  }

  /**
   * Converts a list of Item entities to ItemResponse DTOs.
   *
   * @param items The list of Item entities to convert
   * @return A list of corresponding ItemResponse objects
   */
  public List<ItemResponse> convertToItemResponses(List<Item> items) {
    return items.stream()
      .map(ItemResponse::fromEntity)
      .collect(Collectors.toList());
  }

  /**
   * Converts a list of type strings to a list of ItemType enums.
   * Invalid type strings are ignored.
   *
   * @param typeStrings The list of type strings to convert
   * @return A list of valid ItemType enums
   */
  public List<ItemType> convertToItemTypes(List<String> typeStrings) {
    List<ItemType> itemTypes = new ArrayList<>();
    if (typeStrings != null && !typeStrings.isEmpty()) {
      for (String typeStr : typeStrings) {
        try {
          ItemType type = ItemType.fromString(typeStr);
          itemTypes.add(type);
        } catch (IllegalArgumentException e) {
          // Skip invalid types
        }
      }
    }
    return itemTypes;
  }

  /**
   * Adds a new item based on the provided ItemRequest.
   * This method handles the conversion from DTO to entity and back to response DTO.
   *
   * @param itemRequest The request containing the item details
   * @return The response DTO for the created item
   */
  public ItemResponse addItemFromRequest(ItemRequest itemRequest) {
    Item item = itemRequest.toEntity();
    Item createdItem = addItem(item);
    return ItemResponse.fromEntity(createdItem);
  }

  /**
   * Updates an existing item based on the provided ItemRequest.
   * This method handles the conversion from DTO to entity and back to response DTO.
   *
   * @param id The ID of the item to update
   * @param itemRequest The request containing the updated item details
   * @return The response DTO for the updated item
   */
  public ItemResponse updateItemFromRequest(int id, ItemRequest itemRequest) {
    Item item = itemRequest.toEntity(id);
    Item updatedItem = updateItem(id, item);
    return ItemResponse.fromEntity(updatedItem);
  }


}
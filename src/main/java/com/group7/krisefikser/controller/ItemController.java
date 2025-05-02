package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.ItemRequest;
import com.group7.krisefikser.dto.response.ItemResponse;
import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.Item;
import com.group7.krisefikser.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ItemController handles HTTP requests related to items.
 * It provides endpoints for CRUD operations and filtering/sorting items.
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {
  private final ItemService itemService;

  @Autowired
  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  /**
   * Endpoint to fetch all items.
   *
   * @return a list of all items
   */

  @Operation(
      summary = "Fetch all items",
      description = "Retrieves a list of all items in the database.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved items",
          content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      }
    )
  @GetMapping
  public ResponseEntity<List<ItemResponse>> getAllItems() {
    List<Item> items = itemService.getAllItems();
    List<ItemResponse> itemResponses = items.stream()
        .map(ItemResponse::fromEntity)
        .collect(Collectors.toList());
    return ResponseEntity.ok(itemResponses);
  }

  /**
   * Endpoint to fetch an item by its ID.
   *
   * @param id the ID of the item
   * @return the item with the specified ID
   */
  @Operation(
      summary = "Fetch an item by ID",
      description = "Retrieves an item by its unique ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the item",
          content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      }
    )
  @GetMapping("/{id}")
  public ResponseEntity<ItemResponse> getItemById(@PathVariable int id) {
    try {
      Item item = itemService.getItemById(id);
      return ResponseEntity.ok(ItemResponse.fromEntity(item));
    } catch (RuntimeException e) {
      if (e.getMessage().contains("not found")) {
        return ResponseEntity.notFound().build();
      }
      throw e;
    }
  }

  /**
   * Helper method to convert a list of Item entities to ItemResponse DTOs.
   *
   * @param items The list of Item entities to convert
   * @return A list of corresponding ItemResponse objects
   */
  private List<ItemResponse> convertToItemResponses(List<Item> items) {
    return items.stream()
      .map(ItemResponse::fromEntity)
      .collect(Collectors.toList());
  }

  /**
   * Helper method to validate sort parameters.
   *
   * @param value        The value to validate
   * @param defaultValue The default value if validation fails
   * @param validValues  A list of valid values
   * @return The validated value or the default value
   */
  private String validateSortParameter(String value, String defaultValue,
                                       List<String> validValues) {
    if (value == null || !validValues.contains(value.toLowerCase())) {
      return defaultValue;
    }
    return value.toLowerCase();
  }

  /**
   * Endpoint to filter items based on their types.
   *
   * @param types The list of types to filter by
   * @return A list of filtered items
   */
  @Operation(
      summary = "Filter items by type",
      description = "Retrieves a list of items filtered by the specified types. "
        + "If no types are provided, returns all items.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered items",
          content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  @GetMapping("/filter")
  public ResponseEntity<List<ItemResponse>> filterItems(
      @Parameter(description = "Item types to filter by (e.g., FOOD, DRINK, ACCESSORIES)",
      example = "FOOD", schema = @Schema(type = "array", implementation = String.class))
      @RequestParam(required = false) List<String> types) {
    try {
      List<ItemType> itemTypes = new ArrayList<>();
      if (types != null && !types.isEmpty()) {
        for (String typeStr : types) {
          try {
            ItemType type = ItemType.fromString(typeStr);
            itemTypes.add(type);
          } catch (IllegalArgumentException e) {
            // Skip invalid types
          }
        }
      }

      List<Item> items = itemService.getItemsByTypes(itemTypes);
      return ResponseEntity.ok(convertToItemResponses(items));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to sort items based on a specified field and direction.
   *
   * @param sortBy The field to sort by (default: "name")
   * @param sortDirection The direction to sort (default: "asc")
   * @return A list of sorted items
   */
  @Operation(
      summary = "Sort items",
      description = "Retrieves all items sorted by the specified field and direction. "
      + "Valid sort fields: name, calories. Valid directions: asc, desc.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved sorted items",
          content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  @GetMapping("/sort")
  public ResponseEntity<List<ItemResponse>> sortItems(
      @Parameter(description = "Field to sort by", schema = @Schema(type = "string",
        allowableValues = {"name", "calories"}),
        example = "name")
      @RequestParam(required = false, defaultValue = "name") String sortBy,
      @Parameter(description = "Sort direction", schema = @Schema(type = "string",
        allowableValues = {"asc", "desc"}),
        example = "asc")
      @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
    try {
      sortBy = validateSortParameter(sortBy, "name", List.of("name", "calories"));
      sortDirection = validateSortParameter(sortDirection, "asc", List.of("asc", "desc"));

      List<Item> items = itemService.getSortedItems(sortBy, sortDirection);
      return ResponseEntity.ok(convertToItemResponses(items));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to filter and sort items based on specified criteria.
   *
   * @param types The list of types to filter by
   * @param sortBy The field to sort by (default: "name")
   * @param sortDirection The direction to sort (default: "asc")
   * @return A list of filtered and sorted items
   */
  @Operation(
      summary = "Filter and sort items",
      description = "Retrieves a list of items filtered by the specified types and "
        + "sorted by the specified criteria. If no types are provided, returns all items sorted. "
      + "Valid sort fields: name, calories. Valid directions: asc, desc.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"
          + " filtered and sorted items",
          content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  @GetMapping("/filter-and-sort")
  public ResponseEntity<List<ItemResponse>> filterAndSortItems(
      @Parameter(description = "Item types to filter by (e.g., FOOD, DRINK, ACCESSORIES)",
        example = "FOOD", schema = @Schema(type = "array", implementation = String.class))
      @RequestParam(required = false) List<String> types,
      @Parameter(description = "Field to sort by", schema = @Schema(type = "string",
        allowableValues = {"name", "calories"}),
        example = "name")
      @RequestParam(required = false, defaultValue = "name") String sortBy,
      @Parameter(description = "Sort direction", schema = @Schema(type = "string",
        allowableValues = {"asc", "desc"}),
        example = "asc")
      @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
    try {
      List<ItemType> itemTypes = new ArrayList<>();
      if (types != null && !types.isEmpty()) {
        for (String typeStr : types) {
          try {
            ItemType type = ItemType.fromString(typeStr);
            itemTypes.add(type);
          } catch (IllegalArgumentException e) {
            // Skip invalid types
          }
        }
      }

      sortBy = validateSortParameter(sortBy, "name", List.of("name", "calories"));
      sortDirection = validateSortParameter(sortDirection, "asc", List.of("asc", "desc"));

      List<Item> items = itemService.getFilteredAndSortedItems(itemTypes, sortBy, sortDirection);
      return ResponseEntity.ok(convertToItemResponses(items));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to add a new item.
   *
   * @param itemRequest The request containing the item details
   * @return The created item
   */

  @Operation(
      summary = "Add a new item",
      description = "Creates a new item in the database.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Details of the item to be created",
        required = true,
        content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ItemRequest.class))
      ),
      responses = {
        @ApiResponse(responseCode = "201", description = "Item successfully created",
          content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      }
    )
  @PostMapping
  public ResponseEntity<ItemResponse> addItem(@RequestBody ItemRequest itemRequest) {
    try {
      Item item = itemRequest.toEntity();
      Item createdItem = itemService.addItem(item);
      return ResponseEntity.status(HttpStatus.CREATED).body(ItemResponse.fromEntity(createdItem));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Endpoint to update an existing item.
   *
   * @param id          The ID of the item to update
   * @param itemRequest The request containing the updated item details
   * @return The updated item
   */
  @Operation(
      summary = "Update an existing item",
      description = "Updates an item with the specified ID using the provided item details.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Updated details of the item",
      required = true,
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ItemRequest.class))
       ),
      responses = {
        @ApiResponse(responseCode = "200", description = "Item successfully updated",
          content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  @PutMapping("/{id}")
  public ResponseEntity<ItemResponse> updateItem(
      @Parameter(description = "ID of the item to update", required = true, example = "1")
      @PathVariable int id,
      @RequestBody ItemRequest itemRequest) {
    try {
      Item item = itemRequest.toEntity(id);
      Item updatedItem = itemService.updateItem(id, item);
      return ResponseEntity.ok(ItemResponse.fromEntity(updatedItem));
    } catch (RuntimeException e) {
      if (e.getMessage().contains("not found")) {
        return ResponseEntity.notFound().build();
      } else if (e instanceof IllegalArgumentException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }
      throw e;
    }
  }

  /**
   * Endpoint to delete an item by its ID.
   *
   * @param id The ID of the item to delete
   * @return No content if successful, or an error status
   */
  @Operation(
      summary = "Delete an item",
      description = "Deletes an item with the specified ID. Returns no content if successful.",
      responses = {
        @ApiResponse(responseCode = "204", description = "Item successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      }
      )
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteItem(
      @Parameter(description = "ID of the item to delete", required = true, example = "1")
      @PathVariable int id) {
    try {
      itemService.deleteItem(id);
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      if (e.getMessage().contains("not found")) {
        return ResponseEntity.notFound().build();
      }
      throw e;
    }
  }
}
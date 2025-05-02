package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.ItemFilterRequest;
import com.group7.krisefikser.dto.request.ItemRequest;
import com.group7.krisefikser.dto.request.ItemSortRequest;
import com.group7.krisefikser.dto.response.ItemResponse;
import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.Item;
import com.group7.krisefikser.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ItemController handles HTTP requests related to items.
 * It provides endpoints for CRUD operations and filtering/sorting items.
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {
  private final ItemService itemService;
  private static final Logger logger = Logger.getLogger(ItemController.class.getName());


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
    List<ItemResponse> itemResponses = itemService.convertToItemResponses(items);
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
      logger.severe("Unexpected error retrieving item with ID " + id + ": " + e.getMessage());
      return ResponseEntity.status(500).body(null);
    }
  }

  /**
   * Endpoint to filter items based on their types.
   * This endpoint accepts a request containing a list of item types to filter by
   * and returns a list of items that match those types. If no types are provided,
   * it returns all items.
   *
   * @param request The GetItemsByTypesRequest containing the types to filter by
   * @return A ResponseEntity containing a list of filtered ItemResponse objects
   */
  @Operation(
      summary = "Filter items by type",
      description = "Retrieves a list of items filtered by the specified types. "
      + "If no types are provided, returns all items.",
      parameters = @Parameter(
      name = "types",
      description = "Item types to filter by (e.g., FOOD, DRINK, ACCESSORIES)",
      example = "FOOD",
      schema = @Schema(type = "array", implementation = String.class)
      ),
      responses = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered items",
          content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  @GetMapping("/filter")
  public ResponseEntity<List<ItemResponse>> filterItems(
      @Valid @ModelAttribute ItemFilterRequest request) {

    logger.info("Received request to filter items by types: " + request.getTypes());
    try {
      // Use the service method to convert strings to ItemTypes
      List<ItemType> itemTypes = itemService.convertToItemTypes(request.getTypes());

      List<Item> items = itemService.getItemsByTypes(itemTypes);
      logger.info("Successfully retrieved filtered items");
      return ResponseEntity.ok(itemService.convertToItemResponses(items));
    } catch (Exception e) {
      logger.severe("Unexpected error filtering items: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to sort items based on a specified field and direction.
   * This endpoint accepts a request object containing sort parameters and returns
   * a list of items sorted according to these parameters. The sorting is handled
   * by the ItemService, with validation performed through Jakarta validation annotations.
   *
   * @param request The ItemSortRequest object containing sortBy and sortDirection parameters
   * @return ResponseEntity containing a list of sorted ItemResponse objects
   */
  @Operation(
      summary = "Sort items",
      description = "Retrieves all items sorted by the specified field and direction. "
      + "Valid sort fields: name, calories. Valid directions: asc, desc.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved sorted items",
          content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid sort parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  @GetMapping("/sort")
  public ResponseEntity<List<ItemResponse>> sortItems(
      @Valid @ModelAttribute ItemSortRequest request) {
    logger.info("Received request to sort items by: " + request.getSortBy()
        + " in direction: " + request.getSortDirection());
    try {
      List<Item> items = itemService.getSortedItems(
          request.getSortBy().toLowerCase(),
          request.getSortDirection().toLowerCase());
      logger.info("Successfully sorted items");
      return ResponseEntity.ok(itemService.convertToItemResponses(items));
    } catch (Exception e) {
      logger.severe("Unexpected error sorting items: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to filter and sort items based on specified criteria.
   * This endpoint accepts separate filter and sort request objects and returns
   * a list of items that are both filtered by the specified types and sorted
   * according to the provided sort parameters. The filtering and sorting is
   * performed by the ItemService.
   *
   * @param filterRequest The ItemFilterRequest containing the types to filter by
   * @param sortRequest The ItemSortRequest containing sortBy and sortDirection parameters
   * @return ResponseEntity containing a list of filtered and sorted ItemResponse objects
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
        @ApiResponse(responseCode = "400", description = "Invalid filter or sort parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  @GetMapping("/filter-and-sort")
  public ResponseEntity<List<ItemResponse>> filterAndSortItems(
      @Valid @ModelAttribute ItemFilterRequest filterRequest,
      @Valid @ModelAttribute ItemSortRequest sortRequest) {

    logger.info("Received request to filter and sort items");
    try {
      // Use the service method to convert strings to ItemTypes
      List<ItemType> itemTypes = itemService.convertToItemTypes(filterRequest.getTypes());

      List<Item> items = itemService.getFilteredAndSortedItems(
          itemTypes,
          sortRequest.getSortBy().toLowerCase(),
          sortRequest.getSortDirection().toLowerCase());
      logger.info("Successfully filtered and sorted items");
      return ResponseEntity.ok(itemService.convertToItemResponses(items));
    } catch (Exception e) {
      logger.severe("Unexpected error filtering and sorting items: " + e.getMessage());
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
  public ResponseEntity<ItemResponse> addItem(@Valid @RequestBody ItemRequest itemRequest) {
    logger.info("Received request to add a new item: " + itemRequest.getName());
    try {
      ItemResponse createdItemResponse = itemService.addItemFromRequest(itemRequest);
      logger.info("Successfully added item with ID: " + createdItemResponse.getId());
      return ResponseEntity.status(HttpStatus.CREATED).body(createdItemResponse);
    } catch (Exception e) {
      logger.severe("Error adding item: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
      @Valid @RequestBody ItemRequest itemRequest) {
    logger.info("Received request to update item with ID: " + id);
    try {
      ItemResponse updatedItemResponse = itemService.updateItemFromRequest(id, itemRequest);
      logger.info("Successfully updated item with ID: " + id);
      return ResponseEntity.ok(updatedItemResponse);
    } catch (RuntimeException e) {
      if (e.getMessage().contains("not found")) {
        logger.info("Item not found with ID: " + id);
        return ResponseEntity.notFound().build();
      }
      logger.severe("Error updating item: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    logger.info("Received request to delete item with ID: " + id);
    try {
      itemService.deleteItem(id);
      logger.info("Successfully deleted item with ID: " + id);
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      if (e.getMessage().contains("not found")) {
        logger.info("Item not found with ID: " + id);
        return ResponseEntity.notFound().build();
      }
      logger.severe("Error deleting item: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
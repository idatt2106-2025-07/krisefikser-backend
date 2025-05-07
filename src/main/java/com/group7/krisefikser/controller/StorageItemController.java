package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.StorageItemRequest;
import com.group7.krisefikser.dto.request.StorageItemSearchRequest;
import com.group7.krisefikser.dto.request.StorageItemSortRequest;
import com.group7.krisefikser.dto.response.AggregatedStorageItemResponse;
import com.group7.krisefikser.dto.response.ErrorResponse;
import com.group7.krisefikser.dto.response.StorageItemGroupResponse;
import com.group7.krisefikser.dto.response.StorageItemResponse;
import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.StorageItem;
import com.group7.krisefikser.service.ItemService;
import com.group7.krisefikser.service.StorageItemService;
import com.group7.krisefikser.service.UserService;
import com.group7.krisefikser.utils.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * StorageItemController handles HTTP requests related to storage items.
 * It provides endpoints for CRUD operations and filtering/sorting storage items.
 * Operations are scoped to the household ID of the authenticated user.
 */
@RestController
@RequestMapping("/api/storage-items")
@Tag(name = "Storage Item", description = "Endpoints for managing Storage Items")
public class StorageItemController {
  private final StorageItemService storageItemService;
  private final ItemService itemService;
  private final UserService userService;
  private static final Logger logger = Logger.getLogger(StorageItemController.class.getName());

  /**
   * Constructor for StorageItemController.
   *
   * @param storageItemService The service for managing storage items
   * @param itemService        The service for managing items
   * @param userService        The service for managing users and retrieving the current
   *                           user's household
   */
  @Autowired
  public StorageItemController(
          StorageItemService storageItemService,
          ItemService itemService,
          UserService userService) {
    this.storageItemService = storageItemService;
    this.itemService = itemService;
    this.userService = userService;
  }

  /**
   * Endpoint to fetch all storage items for the authenticated user's household.
   *
   * @return a list of all storage items for the user's household
   */
  @Operation(
          summary = "Fetch all storage items for the user's household",
          description = "Retrieves a list of all storage items for the "
                  + "authenticated user's household.",
          responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved storage items",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StorageItemResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/household")
  public ResponseEntity<List<StorageItemResponse>> getAllStorageItems() {
    try {
      int householdId = userService.getCurrentUserHouseholdId();
      List<StorageItem> storageItems = storageItemService.getAllStorageItems(householdId);
      List<StorageItemResponse> responses = storageItemService
              .convertToStorageItemResponses(storageItems);
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      logger.severe("Error retrieving storage items: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to fetch all shared storage items for the authenticated user's emergency group.
   *
   * @return a list of all shared storage items for the user's emergency group
   */
  @Operation(
          summary = "Fetch all shared storage items for the user's emergency group",
          description = "Retrieves a list of all shared storage items for the authenticated user's "
                  + "emergency group.",
          parameters = {
            @Parameter(name = "types", description = "List of item types to filter by",
                    schema = @Schema(type = "array", implementation = ItemType.class)),
            @Parameter(name = "sortBy", description = "Field to sort by"),
            @Parameter(name = "sortDirection", description = "Sort direction (asc/desc)")
          },
          responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved "
                    + "shared storage items",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StorageItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "No shared storage found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/emergency-group")
  public ResponseEntity<Object> getSharedStorageItemsInGroup(
          @RequestParam(required = false) List<String> types,
          @Valid @ModelAttribute StorageItemSortRequest sortRequest,
          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }
    try {
      List<AggregatedStorageItemResponse> responses = storageItemService
              .getSharedStorageItemsInGroup(
                      types,
                      sortRequest
              );
      return ResponseEntity.ok(responses);
    } catch (NoSuchElementException e) {
      logger.info("No shared storage items found: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
              e.getMessage()
      ));
    } catch (Exception e) {
      logger.severe("Error retrieving shared storage items: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
              "An unexpected error occurred while retrieving shared storage items."
      ));
    }
  }

  /**
   * Endpoint to retrieve storage items in a group by item ID.
   *
   * @param itemId The ID of the item to search for
   * @return A response entity containing a list of storage items in the group
   */
  @Operation(
          summary = "Retrieve storage items in a group by item ID",
          description = "Retrieves a list of storage items in the authenticated user's "
                  + "emergency group by item ID.",
          parameters = {
            @Parameter(name = "itemId", description = "ID of the item to search for",
                    required = true)
          },
          responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved "
                    + "storage items in group",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StorageItemGroupResponse.class))),
            @ApiResponse(responseCode = "404", description = "No shared storage found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/emergency-group/by-item/{itemId}")
  public ResponseEntity<Object> getSharedStorageItemsInGroupByItemId(
          @Parameter(description = "Item ID", required = true)
          @PathVariable int itemId) {
    try {
      List<StorageItemGroupResponse> responses = storageItemService
              .getSharedStorageItemsInGroupByItemId(itemId);
      return ResponseEntity.ok(responses);
    } catch (NoSuchElementException e) {
      logger.info(e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
              e.getMessage()
      ));
    } catch (Exception e) {
      logger.severe("Error retrieving shared storage items: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
              "An unexpected error occurred while retrieving shared storage items."
      ));
    }
  }

  /**
   * Endpoint to find storage items that will expire within a specified number of days.
   *
   * @param days The number of days within which items will expire
   * @return A list of storage items that will expire within the specified number of days
   */
  @Operation(
          summary = "Find expiring storage items",
          description = "Retrieves a list of storage items for the authenticated user's household "
                  + "that will expire within the specified number of days.",
          responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"
                    + "expiring storage items",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StorageItemResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/household/expiring")
  public ResponseEntity<List<StorageItemResponse>> getExpiringStorageItems(
          @RequestParam(defaultValue = "7") int days) {

    try {
      int householdId = userService.getCurrentUserHouseholdId();
      logger.info("Finding storage items expiring within " + days
              + " days for household ID: " + householdId);

      List<StorageItem> storageItems = storageItemService.getExpiringStorageItems(days,
              householdId);
      List<StorageItemResponse> responses = storageItemService
              .convertToStorageItemResponses(storageItems);
      logger.info("Successfully retrieved expiring storage items");
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      logger.severe("Unexpected error finding expiring storage items: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to find storage items by item ID for the authenticated user's household.
   *
   * @param itemId The ID of the item
   * @return A list of storage items that have the specified item ID
   */
  @Operation(
          summary = "Find storage items by item ID",
          description = "Retrieves a list of storage items for the authenticated user's household "
                  + "that have the specified item ID.",
          responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved storage items",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StorageItemResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/household/by-item/{itemId}")
  public ResponseEntity<List<StorageItemResponse>> getStorageItemsByItemId(
          @Parameter(description = "Item ID", required = true)
          @PathVariable int itemId) {

    try {
      int householdId = userService.getCurrentUserHouseholdId();
      logger.info("Finding storage items with item ID: " + itemId
              + " for household ID: " + householdId);

      List<StorageItem> storageItems = storageItemService.getStorageItemsByItemId(itemId,
              householdId);
      List<StorageItemResponse> responses = storageItemService
              .convertToStorageItemResponses(storageItems);
      logger.info("Successfully retrieved storage items with item ID: " + itemId);
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      logger.severe("Unexpected error finding storage items with item ID: " + itemId
              + ": " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to fetch all storage items for the authenticated user's household, aggregated by item.
   *
   * @return a list of aggregated storage items for the user's household
   */
  @Operation(
          summary = "Fetch all storage items for the user's household, aggregated by item",
          description = "Retrieves a list of all storage items for the authenticated "
                  + "user's household, with quantities summed and earliest expiration date "
                  + "found for items of the same type.",
          responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"
                    + "aggregated storage items",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =
                                    AggregatedStorageItemResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/household/aggregated")
  public ResponseEntity<List<AggregatedStorageItemResponse>> getAggregatedStorageItems() {
    try {
      int householdId = userService.getCurrentUserHouseholdId();
      List<AggregatedStorageItemResponse> responses = storageItemService
              .getAggregatedStorageItems(householdId);
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      logger.severe("Error retrieving aggregated storage items: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to sort aggregated storage items for the authenticated user's household.
   *
   * @param request The sort request containing sort parameters
   * @return A list of sorted aggregated storage items
   */
  @Operation(
          summary = "Sort aggregated storage items",
          description = "Retrieves all storage items for the authenticated user's household "
                  + "aggregated by item"
                  + "and sorted by the specified field and direction. "
                  + "Valid sort fields: quantity, expirationDate, name. Valid directions: "
                  + "asc, desc.",
          responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved sorted"
                    + "aggregated storage items",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =
                                    AggregatedStorageItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid sort parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/household/aggregated/sort")
  public ResponseEntity<List<AggregatedStorageItemResponse>> sortAggregatedStorageItems(
          @Valid @ModelAttribute StorageItemSortRequest request) {

    try {
      int householdId = userService.getCurrentUserHouseholdId();
      logger.info("Sorting aggregated storage items by: " + request.getSortBy()
              + " in direction: " + request.getSortDirection()
              + " for household ID: " + householdId);

      List<AggregatedStorageItemResponse> responses = storageItemService.getAggregatedStorageItems(
              householdId,
              request.getSortBy(),
              request.getSortDirection());
      logger.info("Successfully sorted aggregated storage items");
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      logger.severe("Unexpected error sorting aggregated storage items: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to filter aggregated storage items by item type for the
   * authenticated user's household.
   *
   * @param types The list of item types to filter by
   * @return A list of filtered aggregated storage items
   */
  @Operation(
          summary = "Filter aggregated storage items by item type",
          description = "Retrieves a list of aggregated storage items for the "
                  + "authenticated user's household"
                  + "filtered by the item types.",
          responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered"
                    + "aggregated storage items",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =
                                    AggregatedStorageItemResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/household/aggregated/filter-by-type")
  public ResponseEntity<List<AggregatedStorageItemResponse>> filterAggregatedStorageItemsByItemType(
          @RequestParam(required = false) List<String> types) {

    try {
      int householdId = userService.getCurrentUserHouseholdId();
      logger.info("Filtering aggregated storage items by item types: " + types
              + " for household ID: " + householdId);

      // Convert string types to ItemType enums
      List<ItemType> itemTypes = itemService.convertToItemTypes(types);

      List<AggregatedStorageItemResponse> responses = storageItemService
              .getFilteredAndSortedAggregatedItems(
                      householdId,
                      itemTypes,
                      null,
                      null);

      logger.info("Successfully filtered aggregated storage items by item type");
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      logger.severe("Unexpected error filtering aggregated storage items by item type: "
              + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to filter and sort aggregated storage items for the authenticated user's household.
   *
   * @param types       The list of item types to filter by
   * @param sortRequest The sort request containing sort parameters
   * @return A list of filtered and sorted aggregated storage items
   */
  @Operation(
          summary = "Filter and sort aggregated storage items",
          description = "Retrieves a list of aggregated storage items for the "
                  + "authenticated user's household, "
                  + "filtered by item types and sorted by the specified field and direction.",
          responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered "
                    + "and sorted aggregated storage items",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =
                                    AggregatedStorageItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid sort parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/household/aggregated/filter-and-sort")
  public ResponseEntity<List<AggregatedStorageItemResponse>> filterAndSortAggregatedStorageItems(
          @RequestParam(required = false) List<String> types,
          @Valid @ModelAttribute StorageItemSortRequest sortRequest) {

    try {
      int householdId = userService.getCurrentUserHouseholdId();
      logger.info("Filtering and sorting aggregated storage items for household ID: "
              + householdId);

      // Convert string types to ItemType enums
      List<ItemType> itemTypes = itemService.convertToItemTypes(types);

      List<AggregatedStorageItemResponse> responses = storageItemService
              .getFilteredAndSortedAggregatedItems(
                      householdId,
                      itemTypes,
                      sortRequest.getSortBy(),
                      sortRequest.getSortDirection());

      logger.info("Successfully filtered and sorted aggregated storage items");
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      logger.severe("Unexpected error filtering and sorting aggregated storage items: "
              + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to search for aggregated storage items by item name and/or type.
   *
   * @param request The search request containing search parameters
   * @return A list of matching aggregated storage items
   */
  @Operation(
          summary = "Search aggregated storage items",
          description = "Searches for aggregated storage items by item name and/or type for the "
                  + "authenticated user's household. The search is case-insensitive and matches "
                  + "partial item names.",
          responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved "
                    + "matching storage items",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =
                                    AggregatedStorageItemResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/household/aggregated/search")
  public ResponseEntity<List<AggregatedStorageItemResponse>> searchAggregatedStorageItems(
          @Valid @ModelAttribute StorageItemSearchRequest request) {

    try {
      int householdId = userService.getCurrentUserHouseholdId();
      logger.info("Searching aggregated storage items with search term: " + request.getSearchTerm()
              + " and types: " + request.getTypes()
              + " for household ID: " + householdId);

      // Convert string types to ItemType enums
      List<ItemType> itemTypes = itemService.convertToItemTypes(request.getTypes());

      List<AggregatedStorageItemResponse> responses = storageItemService
              .searchAggregatedStorageItems(
                      householdId,
                      request.getSearchTerm(),
                      itemTypes,
                      request.getSortBy(),
                      request.getSortDirection());

      logger.info("Successfully searched aggregated storage items, found "
              + responses.size() + " matches");
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      logger.severe("Unexpected error searching aggregated storage items: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  /**
   * Endpoint to add a new storage item to the authenticated user's household.
   *
   * @param request The request containing the storage item details
   * @return The created storage item
   */
  @Operation(
          summary = "Add a new storage item",
          description = "Creates a new storage item for the authenticated user's household.",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Details of the storage item to be created",
                  required = true,
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = StorageItemRequest.class))
          ),
          responses = {
            @ApiResponse(responseCode = "201", description = "Storage item successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StorageItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @PostMapping
  public ResponseEntity<StorageItemResponse> addStorageItem(
          @Valid @RequestBody StorageItemRequest request) {
    try {
      int householdId = userService.getCurrentUserHouseholdId();
      logger.info("Adding a new storage item for household ID: " + householdId);

      StorageItemResponse response = storageItemService.addStorageItemFromRequest(householdId,
              request);
      logger.info("Successfully added storage item with ID: " + response.getId());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      logger.severe("Error adding storage item: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Endpoint to update an existing storage item in the authenticated user's household.
   *
   * @param id      The ID of the storage item to update
   * @param request The request containing the updated storage item details
   * @return The updated storage item
   */
  @Operation(
          summary = "Update an existing storage item",
          description = "Updates a storage item with the specified ID for the "
                  + "authenticated user's household.",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Updated details of the storage item",
                  required = true,
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = StorageItemRequest.class))
          ),
          responses = {
            @ApiResponse(responseCode = "200", description = "Storage item successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StorageItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Storage item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @PutMapping("/{id}")
  public ResponseEntity<StorageItemResponse> updateStorageItem(
          @PathVariable int id,
          @Valid @RequestBody StorageItemRequest request) {
    try {
      int householdId = userService.getCurrentUserHouseholdId();
      logger.info("Updating storage item with ID: " + id
              + " for household ID: " + householdId);

      StorageItemResponse response = storageItemService.updateStorageItemFromRequest(
              id, householdId, request);
      logger.info("Successfully updated storage item with ID: " + id);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      if (e.getMessage().contains("not found")) {
        logger.info("Storage item not found with ID: " + id);
        return ResponseEntity.notFound().build();
      }
      logger.severe("Error updating storage item: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Endpoint to update an existing shared storage item in the authenticated user's emergency group.
   *
   * @param id      The ID of the shared storage item to update
   * @param request The request containing the updated shared storage item details
   * @return The updated shared storage item
   */
  @Operation(
          summary = "Update an existing shared storage item",
          description = "Updates a shared storage item with the specified ID for the "
                  + "authenticated user's emergency group.",
          parameters = {
            @Parameter(name = "id", description = "ID of the shared storage item to update",
                    required = true)
          },
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Updated details of the shared storage item",
                  required = true,
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = StorageItemRequest.class))
          ),
          responses = {
            @ApiResponse(responseCode = "200", description = "Shared storage item "
                    + "successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StorageItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Shared storage item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @PutMapping("/emergency-group/{id}")
  public ResponseEntity<Object> updateSharedStorageItem(
          @PathVariable int id,
          @Valid @RequestBody StorageItemRequest request) {
    try {

      StorageItemResponse response = storageItemService.updateSharedStorageItem(
              id, request);
      logger.info("Successfully updated shared storage item with ID: " + id);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      logger.info(e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
              e.getMessage()
      ));
    } catch (NoSuchElementException e) {
      logger.info(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
              e.getMessage()
      ));
    } catch (Exception e) {
      logger.severe("Error updating shared storage item: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("An unexpected error occurred while updating the "
                        + "shared storage item.")
      );
    }
  }


  /**
   * Endpoint to delete a storage item by its ID from the authenticated user's household.
   *
   * @param id The ID of the storage item to delete
   * @return No content if successful, or an error status
   */
  @Operation(
          summary = "Delete a storage item",
          description = "Deletes a storage item with the specified ID from the "
                  + "authenticated user's household. "
                  + "Returns no content if successful.",
          responses = {
            @ApiResponse(responseCode = "204", description = "Storage item successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Storage item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStorageItem(
          @Parameter(description = "Storage item ID", required = true)
          @PathVariable int id) {

    try {
      int householdId = userService.getCurrentUserHouseholdId();
      logger.info("Deleting storage item with ID: " + id
              + " for household ID: " + householdId);

      storageItemService.deleteStorageItem(id, householdId);
      logger.info("Successfully deleted storage item with ID: " + id);
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      if (e.getMessage().contains("not found")) {
        logger.info("Storage item not found with ID: " + id);
        return ResponseEntity.notFound().build();
      }
      logger.severe("Error deleting storage item: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
package com.group7.krisefikser.service.item;

import static java.util.stream.Collectors.toList;

import com.group7.krisefikser.dto.request.item.ChangeStorageItemSharedStatusRequest;
import com.group7.krisefikser.dto.request.item.StorageItemRequest;
import com.group7.krisefikser.dto.request.item.StorageItemSortRequest;
import com.group7.krisefikser.dto.response.item.AggregatedStorageItemResponse;
import com.group7.krisefikser.dto.response.item.ItemResponse;
import com.group7.krisefikser.dto.response.item.StorageItemGroupResponse;
import com.group7.krisefikser.dto.response.item.StorageItemResponse;
import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.household.Household;
import com.group7.krisefikser.model.item.Item;
import com.group7.krisefikser.model.item.StorageItem;
import com.group7.krisefikser.repository.household.HouseholdRepository;
import com.group7.krisefikser.repository.item.ItemRepo;
import com.group7.krisefikser.repository.item.StorageItemRepo;
import com.group7.krisefikser.service.household.HouseholdService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is a service for managing storage items.
 * It provides methods to get, add, update, delete, sort and filter storage items.
 * It uses StorageItemRepo to interact with the database.
 */
@Service
@RequiredArgsConstructor
public class StorageItemService {
  private final StorageItemRepo storageItemRepo;
  private final ItemRepo itemRepo;
  private final HouseholdService householdService;
  private final HouseholdRepository householdRepository;
  private final ItemService itemService;
  private static final Logger logger = Logger.getLogger(StorageItemService.class.getName());


  /**
   * Retrieves all storage items for a specific household from the repository.
   *
   * @param householdId The ID of the household to retrieve storage items for.
   * @return A list of all storage items available for the specified household.
   */
  public List<StorageItem> getAllStorageItems(int householdId) {
    return storageItemRepo.getAllStorageItems(householdId);
  }

  /**
   * Retrieves all shared storage items for a specific group from the repository.
   * Using the group ID for the current user, it fetches all storage items that are
   * marked as shared.
   *
   * @return A list of all shared storage items available for the specified group.
   */
  public List<AggregatedStorageItemResponse> getSharedStorageItemsInGroup(
          List<String> itemTypesString,
          StorageItemSortRequest sortRequest) {
    List<ItemType> itemTypes = itemService.convertToItemTypes(itemTypesString);
    long groupId = householdService.getGroupIdForCurrentUser();
    List<StorageItem> storageItems = storageItemRepo.getAllSharedStorageItemsInGroup(groupId);

    List<AggregatedStorageItemResponse> aggregatedItems =
            aggregateStorageItems(storageItems, null, null);

    return filterAndSortAggregatedStorageItems(
            aggregatedItems,
            itemTypes,
            sortRequest.getSortBy(),
            sortRequest.getSortDirection()
    );
  }

  /**
   * Retrieves storage items by their item ID for a specific household.
   *
   * @param itemId      The item ID of the storage items to retrieve.
   * @param householdId The ID of the household the storage items belong to.
   * @return A list of storage items of the specified item.
   */
  public List<StorageItem> getStorageItemsByItemId(int itemId, int householdId) {
    return storageItemRepo.findByItemId(itemId, householdId);
  }

  /**
   * Retrieves all storage items for a specific group ID and item ID.
   *
   * @param itemId The itemID of the items to retrieve.
   * @return The storage item with the specified ID, or null if not found.
   */
  public List<StorageItemGroupResponse> getSharedStorageItemsInGroupByItemId(
          int itemId) {
    Long groupId = householdService.getGroupIdForCurrentUser();

    List<StorageItem> storageItems = storageItemRepo
            .getSharedStorageItemsInGroupByItemId(groupId, itemId);


    return storageItems.stream()
            .map(storageItem -> new StorageItemGroupResponse(
                    convertToStorageItemResponse(storageItem),
                    householdService.getHouseholdNameById((long) storageItem.getHouseholdId())
            ))
            .toList();
  }


  /**
   * Adds a new storage item to the repository after validating it.
   * This method ensures that the item meets all validation criteria
   * before delegating the addition to the repository.
   *
   * @param storageItem The storage item to be added.
   * @return The added storage item with its generated ID.
   */
  public StorageItem addStorageItem(StorageItem storageItem) {
    validateStorageItem(storageItem);

    // Ensure the referenced item exists
    if (!itemExists(storageItem.getItemId())) {
      throw new RuntimeException("Item not found with id: " + storageItem.getItemId());
    }

    return storageItemRepo.add(storageItem);
  }

  /**
   * Updates an existing storage item in the repository after validating it.
   * This method ensures that the item meets all validation criteria
   * before delegating the update to the repository.
   *
   * @param id          The ID of the storage item to be updated.
   * @param householdId The ID of the household the storage item belongs to.
   * @param storageItem The updated storage item details.
   * @return The updated storage item.
   */
  public StorageItem updateStorageItem(int id, int householdId, StorageItem storageItem) {
    if (!storageItemExists(id, householdId)) {
      throw new RuntimeException("Storage item not found with id: " + id
              + " in household: " + householdId);
    }

    validateStorageItem(storageItem);

    // Ensure the referenced item exists
    if (!itemExists(storageItem.getItemId())) {
      throw new RuntimeException("Item not found with id: " + storageItem.getItemId());
    }

    // Ensure household ID is not changed
    storageItem.setId(id);
    storageItem.setHouseholdId(householdId);
    return storageItemRepo.update(storageItem);
  }

  /**
   * Updates a shared storage item from the request.
   * This method checks if the item is shared and if the user belongs to the same group
   * before allowing the update.
   *
   * @param id      The ID of the storage item to be updated.
   * @param request The request containing the updated storage item details.
   * @return The updated storage item response.
   */
  public StorageItemResponse updateSharedStorageItem(int id,
                                                     StorageItemRequest request) {

    Long userGroupId = householdService.getGroupIdForCurrentUser();
    StorageItem existingItem = storageItemRepo.findById(id).orElseThrow(
        () -> new NoSuchElementException("Storage item not found with id: " + id)
    );

    if (!existingItem.isShared()) {
      throw new IllegalArgumentException("Item is not shared, user is not allowed to "
        + "update");
    }

    Household household = householdRepository.getHouseholdById((long) existingItem.getHouseholdId())
        .orElseThrow(() -> new NoSuchElementException("Household not found with id: "
        + existingItem.getHouseholdId()));

    if (!household.getEmergencyGroupId().equals(userGroupId)) {
      throw new IllegalArgumentException("User is not allowed to update this item");
    }

    StorageItem updatedItem = request.updateExistingEntity(existingItem);

    validateStorageItem(updatedItem);

    if (!itemExists(updatedItem.getItemId())) {
      throw new RuntimeException("Item not found with id: " + updatedItem.getItemId());
    }

    StorageItem result = storageItemRepo.update(updatedItem);
    return convertToStorageItemResponse(result);
  }

  /**
   * Deletes a storage item from the repository by its ID.
   *
   * @param id          The ID of the storage item to be deleted.
   * @param householdId The ID of the household the storage item belongs to.
   */
  public void deleteStorageItem(int id, int householdId) {
    Long groupId = householdService.getGroupIdForCurrentUser();
    StorageItem storageItem = storageItemRepo.findById(id)
                    .orElseThrow(() -> new NoSuchElementException(
                    "Storage item not found with id: " + id));
    Household household = householdRepository.getHouseholdById((long) householdId)
            .orElseThrow(() -> new RuntimeException("Household not found with id: "
                    + householdId));

    if (storageItem.getHouseholdId() != householdId
            && (!Objects.equals(groupId, household.getEmergencyGroupId())
                || !storageItem.isShared())) {
      throw new IllegalArgumentException("User is not allowed to delete this item");
    }

    storageItemRepo.deleteById(id, householdId);
  }

  /**
   * Retrieves storage items that are about to expire for a specific household.
   *
   * @param days        The number of days within which items will expire.
   * @param householdId The ID of the household to retrieve expiring items for.
   * @return A list of storage items that will expire within the specified number of days.
   */
  public List<StorageItem> getExpiringStorageItems(int days, int householdId) {
    return storageItemRepo.findExpiringItems(days, householdId);
  }

  /**
   * Validates the storage item properties before adding or updating it.
   * This method checks if the expiration date, quantity, household ID, and item ID are valid.
   *
   * @param storageItem The storage item to be validated.
   */
  private void validateStorageItem(StorageItem storageItem) {
    // Expiration date validation
    if (storageItem.getExpirationDate() == null) {
      throw new IllegalArgumentException("Expiration date cannot be null");
    }

    // Quantity validation
    if (storageItem.getQuantity() < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative");
    }

    // Household ID validation
    if (storageItem.getHouseholdId() <= 0) {
      throw new IllegalArgumentException("Invalid household ID");
    }

    // Item ID validation
    if (storageItem.getItemId() <= 0) {
      throw new IllegalArgumentException("Invalid item ID");
    }
  }

  /**
   * Checks if a storage item exists in the repository by its ID for a specific household.
   *
   * @param id          The ID of the storage item to check.
   * @param householdId The ID of the household the storage item belongs to.
   * @return true if the storage item exists, false otherwise.
   */
  public boolean storageItemExists(int id, int householdId) {
    return storageItemRepo.findById(id).isPresent();
  }

  /**
   * Checks if an item exists in the repository by its ID.
   *
   * @param id The ID of the item to check.
   * @return true if the item exists, false otherwise.
   */
  private boolean itemExists(int id) {
    return itemRepo.findById(id).isPresent();
  }

  /**
   * Converts a single StorageItem entity to a StorageItemResponse DTO with item details.
   *
   * @param storageItem The StorageItem entity
   * @return A StorageItemResponse with item details
   */
  public StorageItemResponse convertToStorageItemResponse(StorageItem storageItem) {
    try {
      Item item = itemRepo.findById(storageItem.getItemId()).orElse(null);
      ItemResponse itemResponse = null;
      if (item != null) {
        itemResponse = ItemResponse.fromEntity(item);
      }
      return StorageItemResponse.fromEntityWithItem(storageItem, itemResponse);
    } catch (Exception e) {
      return StorageItemResponse.fromEntity(storageItem);
    }
  }

  /**
   * Converts a list of StorageItem entities to StorageItemResponse DTOs.
   *
   * @param storageItems The list of StorageItem entities
   * @return A list of StorageItemResponse DTOs
   */
  public List<StorageItemResponse> convertToStorageItemResponses(List<StorageItem> storageItems) {
    return storageItems.stream()
            .map(this::convertToStorageItemResponse)
            .toList();
  }

  /**
   * Adds a new storage item based on the provided request DTO.
   *
   * @param householdId The household ID to assign to the new storage item
   * @param request     The request containing the storage item details
   * @return The response DTO for the created storage item
   */
  public StorageItemResponse addStorageItemFromRequest(int householdId,
                                                       StorageItemRequest request) {
    StorageItem storageItem = request.toEntity();
    storageItem.setHouseholdId(householdId);
    StorageItem createdStorageItem = addStorageItem(storageItem);
    return convertToStorageItemResponse(createdStorageItem);
  }

  /**
   * Updates an existing storage item based on the provided request DTO.
   *
   * @param id          The ID of the storage item to update
   * @param householdId The household ID the storage item belongs to
   * @param request     The request containing the updated storage item details
   * @return The response DTO for the updated storage item
   */
  public StorageItemResponse updateStorageItemFromRequest(int id, int householdId,
                                                          StorageItemRequest request) {
    StorageItem storageItem = request.toEntity();
    storageItem.setId(id);
    storageItem.setHouseholdId(householdId);
    StorageItem updatedStorageItem = updateStorageItem(id, householdId, storageItem);
    return convertToStorageItemResponse(updatedStorageItem);
  }

  /**
   * Aggregates storage items by item ID for a specific household.
   *
   * @param householdId The ID of the household
   * @return A list of aggregated storage item responses
   */
  public List<AggregatedStorageItemResponse> getAggregatedStorageItems(int householdId) {
    return getAggregatedStorageItems(householdId, null, null);
  }

  /**
   * Aggregates storage items by item ID for a specific household,
   * with optional sorting.
   *
   * @param householdId   The ID of the household
   * @param sortBy        The field to sort by (e.g., "quantity", "expirationDate", "name")
   * @param sortDirection The direction of sorting (e.g., "asc" or "desc")
   * @return A list of aggregated storage item responses
   */
  public List<AggregatedStorageItemResponse> getAggregatedStorageItems(
          int householdId,
          String sortBy,
          String sortDirection) {
    List<StorageItem> allItems = storageItemRepo.getAllStorageItems(householdId);

    return aggregateStorageItems(allItems, sortBy, sortDirection);
  }

  /**
   * Aggregates storage items by item ID and creates a list of aggregated responses.
   *
   * @param storageItems  The list of all storage items
   * @param sortBy        The field to sort by (e.g., "quantity", "expirationDate", "name")
   * @param sortDirection The direction of sorting (e.g., "asc" or "desc")
   * @return A list of aggregated storage item responses
   */
  public List<AggregatedStorageItemResponse> aggregateStorageItems(
          List<StorageItem> storageItems,
          String sortBy,
          String sortDirection) {
    Map<Integer, List<StorageItem>> groupedByItemId = storageItems.stream()
            .collect(Collectors.groupingBy(StorageItem::getItemId));

    // Create aggregated responses
    List<AggregatedStorageItemResponse> result = new ArrayList<>();

    for (Map.Entry<Integer, List<StorageItem>> entry : groupedByItemId.entrySet()) {
      int itemId = entry.getKey();
      List<StorageItem> items = entry.getValue();

      // Calculate total quantity
      double totalQuantity = items.stream()
              .mapToDouble(StorageItem::getQuantity)
              .sum();

      // Find earliest expiration date
      LocalDateTime earliestDate = items.stream()
              .map(StorageItem::getExpirationDate)
              .min(LocalDateTime::compareTo)
              .orElse(null);

      // Get item details
      Item item = null;
      try {
        item = itemRepo.findById(itemId).orElse(null);
      } catch (Exception e) {
        // Log error but continue processing
        logger.warning("Could not fetch item with ID " + itemId + ": " + e.getMessage());
      }

      ItemResponse itemResponse = item != null ? ItemResponse.fromEntity(item) : null;

      // Create the aggregated response
      AggregatedStorageItemResponse aggregated = new AggregatedStorageItemResponse(
              itemId,
              itemResponse,
              totalQuantity,
              earliestDate
      );

      result.add(aggregated);
    }

    // Apply sorting if provided
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<AggregatedStorageItemResponse> comparator =
              createAggregatedComparator(sortBy, sortDirection);
      result.sort(comparator);
    }

    return result;
  }

  /**
   * Creates a comparator for sorting aggregated storage items.
   *
   * @param sortBy        The field to sort by
   * @param sortDirection The direction of sorting
   * @return A comparator for sorting aggregated storage items
   */
  private Comparator<AggregatedStorageItemResponse> createAggregatedComparator(
          String sortBy, String sortDirection) {
    Comparator<AggregatedStorageItemResponse> comparator = switch (sortBy.toLowerCase()) {
      case "quantity" -> Comparator.comparing(AggregatedStorageItemResponse::getTotalQuantity);
      case "expirationdate" -> Comparator.comparing(
              AggregatedStorageItemResponse::getEarliestExpirationDate,
              Comparator.nullsLast(Comparator.naturalOrder())
      );
      case "name" -> Comparator.comparing(
              response -> response.getItem() != null ? response.getItem().getName() : "",
              String.CASE_INSENSITIVE_ORDER
      );
      default -> Comparator.comparing(AggregatedStorageItemResponse::getItemId);
    };

    return "desc".equalsIgnoreCase(sortDirection) ? comparator.reversed() : comparator;
  }

  /**
   * Aggregates storage items by item ID for a specific household,
   * with optional filtering and sorting.
   *
   * @param householdId   The ID of the household
   * @param itemTypes     The item types to filter by
   * @param sortBy        The field to sort by
   * @param sortDirection The direction of sorting
   * @return A list of filtered and sorted aggregated storage item responses
   */
  public List<AggregatedStorageItemResponse> getFilteredAndSortedAggregatedItems(
          int householdId,
          List<ItemType> itemTypes,
          String sortBy,
          String sortDirection) {

    // Get aggregated items
    List<AggregatedStorageItemResponse> aggregatedItems =
            getAggregatedStorageItems(householdId, null, null);


    return filterAndSortAggregatedStorageItems(
            aggregatedItems,
            itemTypes,
            sortBy,
            sortDirection
    );
  }

  private List<AggregatedStorageItemResponse> filterAndSortAggregatedStorageItems(
          List<AggregatedStorageItemResponse> aggregatedItems,
          List<ItemType> itemTypes,
          String sortBy,
          String sortDirection) {

    List<AggregatedStorageItemResponse> filteredItems = aggregatedItems;
    if (itemTypes != null && !itemTypes.isEmpty()) {
      filteredItems = aggregatedItems.stream()
              .filter(aggregated ->
                      aggregated.getItem() != null
                              &&
                              itemTypes.contains(aggregated.getItem().getType()))
              .collect(toList());
    }

    // Apply sorting if provided
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<AggregatedStorageItemResponse> comparator =
              createAggregatedComparator(sortBy, sortDirection);
      filteredItems.sort(comparator);
    }

    return filteredItems;
  }

  /**
   * Searches for aggregated storage items by item name and/or type.
   *
   * @param householdId   The ID of the household
   * @param searchTerm    The search term to match against item names (can be null)
   * @param itemTypes     The item types to filter by (can be null or empty)
   * @param sortBy        The field to sort by (can be null)
   * @param sortDirection The direction of sorting (can be null)
   * @return A list of matching aggregated storage item responses
   */
  public List<AggregatedStorageItemResponse> searchAggregatedStorageItems(
          int householdId,
          String searchTerm,
          List<ItemType> itemTypes,
          String sortBy,
          String sortDirection) {

    // Get all aggregated items for this household
    List<AggregatedStorageItemResponse> allItems = getAggregatedStorageItems(householdId);

    // Apply filtering based on search term and item types
    List<AggregatedStorageItemResponse> filteredItems = allItems.stream()
            .filter(item -> {
              boolean matchesSearchTerm = true;
              boolean matchesItemType = true;

              // Filter by search term if provided (case-insensitive partial match)
              if (searchTerm != null && !searchTerm.trim().isEmpty() && item.getItem() != null) {
                matchesSearchTerm = item.getItem().getName().toLowerCase()
                        .contains(searchTerm.toLowerCase().trim());
              }

              // Filter by item types if provided
              if (itemTypes != null && !itemTypes.isEmpty() && item.getItem() != null) {
                matchesItemType = itemTypes.contains(item.getItem().getType());
              }

              return matchesSearchTerm && matchesItemType;
            })
            .collect(toList());

    // Apply sorting if provided
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<AggregatedStorageItemResponse> comparator =
              createAggregatedComparator(sortBy, sortDirection);
      filteredItems.sort(comparator);
    }
    return filteredItems;
  }

  /**
   * Changes the shared status of a storage item and updates its quantity.
   * If the quantity is changed, a new storage item is created with the updated quantity.
   *
   * @param id      The ID of the storage item to update
   * @param request The request containing the new shared status and quantity
   * @return A list of updated storage item responses
   */
  @Transactional
  public List<StorageItemResponse> updateStorageItemSharedStatus(
          int id, long householdId, ChangeStorageItemSharedStatusRequest request) {
    StorageItem storageItem = storageItemRepo.findById(id).orElseThrow(
            () -> new NoSuchElementException("Storage item not found with id: " + id)
    );
    if (storageItem.getHouseholdId() != householdId) {
      throw new IllegalArgumentException("User is not allowed to update this item");
    }
    if (storageItem.isShared() == Boolean.TRUE.equals(request.getIsShared())) {
      throw new IllegalArgumentException("Storage item already has the requested shared status");
    }

    double quantityToMove = request.getQuantity();
    double previousQuantity = storageItem.getQuantity();
    if (quantityToMove > previousQuantity) {
      throw new IllegalArgumentException("Cannot move more than the available quantity");
    } else if (quantityToMove == previousQuantity) {
      storageItem.setShared(request.getIsShared());
      return convertToStorageItemResponses(List.of(storageItemRepo.update(storageItem)));
    } else {
      StorageItem newStorageItem = new StorageItem();
      newStorageItem.setExpirationDate(storageItem.getExpirationDate());
      newStorageItem.setQuantity(previousQuantity - quantityToMove);
      newStorageItem.setHouseholdId(storageItem.getHouseholdId());
      newStorageItem.setItemId(storageItem.getItemId());
      newStorageItem.setShared(storageItem.isShared());

      storageItem.setQuantity(quantityToMove);
      storageItem.setShared(request.getIsShared());

      List<StorageItem> updatedItems = List.of(
              storageItemRepo.update(storageItem),
              storageItemRepo.add(newStorageItem)
      );

      return convertToStorageItemResponses(updatedItems);
    }
  }

}
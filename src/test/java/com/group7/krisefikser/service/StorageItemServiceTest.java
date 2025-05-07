package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.StorageItemSortRequest;
import com.group7.krisefikser.dto.response.AggregatedStorageItemResponse;
import com.group7.krisefikser.dto.response.ItemResponse;
import com.group7.krisefikser.dto.response.StorageItemGroupResponse;
import com.group7.krisefikser.dto.response.StorageItemResponse;
import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.Item;
import com.group7.krisefikser.model.StorageItem;
import com.group7.krisefikser.repository.ItemRepo;
import com.group7.krisefikser.repository.StorageItemRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the StorageItemService class.
 * This class tests the methods of the StorageItemService class
 * to ensure they behave as expected.
 */
@ExtendWith(MockitoExtension.class)
class StorageItemServiceTest {
  @Mock
  private StorageItemRepo storageItemRepo;
  @Mock
  ItemService itemService;

  @Mock
  private ItemRepo itemRepo;

  @Mock
  HouseholdService householdService;

  @InjectMocks
  private StorageItemService storageItemService;

  /**
   * Test for getAllStorageItems method.
   * This test verifies that the method returns all storage items for a specific household.
   */
  @Test
  void getAllStorageItems_shouldReturnAllItemsForHousehold() {
    // Setup
    int householdId = 1;
    List<StorageItem> mockItems = Arrays.asList(
            createStorageItem(1, 101, householdId, 5, LocalDateTime.now().plusDays(10)),
            createStorageItem(2, 102, householdId, 3, LocalDateTime.now().plusDays(5))
    );

    when(storageItemRepo.getAllStorageItems(householdId)).thenReturn(mockItems);

    // Execute
    List<StorageItem> result = storageItemService.getAllStorageItems(householdId);

    // Verify
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(1, result.get(0).getId());
    assertEquals(2, result.get(1).getId());
    verify(storageItemRepo, times(1)).getAllStorageItems(householdId);
  }

  @Test
  void getAllSharedStorageItemsInGroup_returnsListOfStorageItemResponseSortedAndFiltered() {
    LocalDateTime shortestExpirationDate = LocalDateTime.now().plusDays(5);

    List<StorageItem> mockItems = Arrays.asList(
            createStorageItem(1, 1, 1, 5, LocalDateTime.now().plusDays(10)),
            createStorageItem(2, 2, 3, 4, shortestExpirationDate),
            createStorageItem(3, 2, 1, 3, LocalDateTime.now().plusDays(7)),
            createStorageItem(4, 3, 2, 3, LocalDateTime.now().plusDays(8)),
            createStorageItem(5, 4, 1, 6, LocalDateTime.now().plusDays(6))
    );

    StorageItemSortRequest sortRequest = new StorageItemSortRequest();
    sortRequest.setSortBy("quantity");
    sortRequest.setSortDirection("desc");

    List<String> typesString = List.of("DRINK", "FOOD");
    List<ItemType> types = List.of(ItemType.DRINK, ItemType.FOOD);

    when(itemService.convertToItemTypes(typesString)).thenReturn(types);
    when(itemRepo.findById(1)).thenReturn(Optional.of(new Item(
            1, "Item 1", "units", 100, ItemType.DRINK)));
    when(itemRepo.findById(2)).thenReturn(Optional.of(new Item(
            2, "Item 2", "units", 200, ItemType.FOOD)));
    when(itemRepo.findById(3)).thenReturn(Optional.of(new Item(
            3, "Item 3", "units", 300, ItemType.ACCESSORIES)));
    when(itemRepo.findById(4)).thenReturn(Optional.of(new Item(
            4, "Item 4", "units", 400, ItemType.FOOD)));


    when(householdService.getGroupIdForCurrentUser()).thenReturn(1L);
    when(storageItemRepo.getAllSharedStorageItemsInGroup(1L)).thenReturn(mockItems);


    List<AggregatedStorageItemResponse> result = storageItemService.getSharedStorageItemsInGroup(typesString, sortRequest);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(2, result.get(0).getItemId());
    assertEquals(7, result.get(0).getTotalQuantity());
    assertEquals(shortestExpirationDate, result.get(0).getEarliestExpirationDate());
    assertEquals(ItemType.FOOD, result.get(0).getItem().getType());
    System.out.println(result);
    assertEquals(4, result.get(1).getItemId());
    assertEquals(1, result.get(2).getItemId());
    verify(storageItemRepo, times(1)).getAllSharedStorageItemsInGroup(1L);
    verify(householdService, times(1)).getGroupIdForCurrentUser();
  }

  @Test
  void getAllSharedStorageItemsInGroup_noItemsFromRepo_returnsEmptyList() {
    List<StorageItem> mockItems = List.of();

    StorageItemSortRequest sortRequest = new StorageItemSortRequest();
    sortRequest.setSortBy("quantity");
    sortRequest.setSortDirection("desc");

    List<String> types = List.of("DRINK", "FOOD");

    when(householdService.getGroupIdForCurrentUser()).thenReturn(1L);
    when(storageItemRepo.getAllSharedStorageItemsInGroup(1L)).thenReturn(mockItems);

    List<AggregatedStorageItemResponse> result = storageItemService.getSharedStorageItemsInGroup(types, sortRequest);

    assertNotNull(result);
    assertEquals(0, result.size());
    verify(storageItemRepo, times(1)).getAllSharedStorageItemsInGroup(1L);
    verify(householdService, times(1)).getGroupIdForCurrentUser();
  }

  @Test
  void getAllSharedStorageItemsFromGroup_invalidTypeGetsIgnored() {
    LocalDateTime shortestExpirationDate = LocalDateTime.now().plusDays(5);

    List<StorageItem> mockItems = Arrays.asList(
            createStorageItem(1, 1, 1, 5, LocalDateTime.now().plusDays(10)),
            createStorageItem(2, 2, 3, 4, shortestExpirationDate),
            createStorageItem(3, 2, 1, 3, LocalDateTime.now().plusDays(7)),
            createStorageItem(4, 3, 2, 3, LocalDateTime.now().plusDays(8)),
            createStorageItem(5, 4, 1, 6, LocalDateTime.now().plusDays(6))
    );

    StorageItemSortRequest sortRequest = new StorageItemSortRequest();
    sortRequest.setSortBy("quantity");
    sortRequest.setSortDirection("desc");

    List<String> typesString = List.of("DRINK", "food", "INVALID");
    List<ItemType> types = List.of(ItemType.DRINK, ItemType.FOOD);

    when(itemService.convertToItemTypes(typesString)).thenReturn(types);
    when(itemRepo.findById(1)).thenReturn(Optional.of(new Item(
            1, "Item 1", "units", 100, ItemType.DRINK)));
    when(itemRepo.findById(2)).thenReturn(Optional.of(new Item(
            2, "Item 2", "units", 200, ItemType.FOOD)));
    when(itemRepo.findById(3)).thenReturn(Optional.of(new Item(
            3, "Item 3", "units", 300, ItemType.ACCESSORIES)));
    when(itemRepo.findById(4)).thenReturn(Optional.of(new Item(
            4, "Item 4", "units", 400, ItemType.FOOD)));


    when(householdService.getGroupIdForCurrentUser()).thenReturn(1L);
    when(storageItemRepo.getAllSharedStorageItemsInGroup(1L)).thenReturn(mockItems);

    List<AggregatedStorageItemResponse> result = storageItemService.getSharedStorageItemsInGroup(typesString, sortRequest);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(2, result.get(0).getItemId());
    assertEquals(7, result.get(0).getTotalQuantity());
    assertEquals(shortestExpirationDate, result.get(0).getEarliestExpirationDate());
    assertEquals(ItemType.FOOD, result.get(0).getItem().getType());
    System.out.println(result);
    assertEquals(4, result.get(1).getItemId());
    assertEquals(1, result.get(2).getItemId());
    verify(storageItemRepo, times(1)).getAllSharedStorageItemsInGroup(1L);
    verify(householdService, times(1)).getGroupIdForCurrentUser();
  }

  /**
   * Test for getStorageItemsByItemId method.
   * This test verifies that the method returns storage items by item ID for a specific household.
   */
  @Test
  void getStorageItemsByItemId_shouldReturnItemsForItemIdAndHousehold() {
    // Setup
    int itemId = 101;
    int householdId = 1;
    List<StorageItem> mockItems = Arrays.asList(
            createStorageItem(1, itemId, householdId, 5, LocalDateTime.now().plusDays(10)),
            createStorageItem(2, itemId, householdId, 3, LocalDateTime.now().plusDays(5))
    );

    when(storageItemRepo.findByItemId(itemId, householdId)).thenReturn(mockItems);

    // Execute
    List<StorageItem> result = storageItemService.getStorageItemsByItemId(itemId, householdId);

    // Verify
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(itemId, result.get(0).getItemId());
    assertEquals(itemId, result.get(1).getItemId());
    verify(storageItemRepo, times(1)).findByItemId(itemId, householdId);
  }

  @Test
  void getSharedStorageItemsInGroupByItemId_valid_returnsStorageItemsWithName() {
    int itemId = 1;
    long groupId = 1L;

    StorageItem item1 = createStorageItem(1, itemId, 1, 5, LocalDateTime.now().plusDays(10));
    StorageItem item2 = createStorageItem(2, itemId, 2, 3, LocalDateTime.now().plusDays(5));
    StorageItem item3 = createStorageItem(3, itemId, 2, 2, LocalDateTime.now().plusDays(7));
    List<StorageItem> mockItems = Arrays.asList(item1, item2, item3);

    try (MockedStatic<StorageItemResponse> mocked = Mockito.mockStatic(StorageItemResponse.class)) {

      when(householdService.getGroupIdForCurrentUser()).thenReturn(groupId);

      mocked.when(() -> StorageItemResponse.fromEntity(item1)).thenReturn(new StorageItemResponse());
      mocked.when(() -> StorageItemResponse.fromEntity(item2)).thenReturn(new StorageItemResponse());
      mocked.when(() -> StorageItemResponse.fromEntity(item3)).thenReturn(new StorageItemResponse());

      when(storageItemRepo.getSharedStorageItemsInGroupByItemId(groupId, itemId))
              .thenReturn(mockItems);
      when(householdService.getHouseholdNameById(anyLong())).thenReturn("name");

      List<StorageItemGroupResponse> result = storageItemService.getSharedStorageItemsInGroupByItemId(itemId);
      assertNotNull(result);
      assertEquals(3, result.size());
      verify(householdService, times(1)).getGroupIdForCurrentUser();
      verify(storageItemRepo, times(1)).getSharedStorageItemsInGroupByItemId(groupId, itemId);
      verify(householdService, times(3)).getHouseholdNameById(anyLong());
    }
  }

  @Test
  void getSharedStorageItemsInGroupByItemId_validNoGroup_throwsNoSuchElementException() {
    int itemId = 1;

    when(householdService.getGroupIdForCurrentUser()).thenThrow(new NoSuchElementException("No group found"));

    NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
            storageItemService.getSharedStorageItemsInGroupByItemId(itemId)
    );

    assertEquals("No group found", exception.getMessage());
    verify(householdService, times(1)).getGroupIdForCurrentUser();
  }

  @Test
  void getSharedStorageItemsInGroupByItemId_noItems_returnsEmptyList() {
    int itemId = 1;
    long groupId = 1L;
    try (MockedStatic<StorageItemResponse> mocked = Mockito.mockStatic(StorageItemResponse.class)) {

      when(householdService.getGroupIdForCurrentUser()).thenReturn(groupId);

      when(storageItemRepo.getSharedStorageItemsInGroupByItemId(groupId, itemId))
              .thenReturn(List.of());

      List<StorageItemGroupResponse> result = storageItemService.getSharedStorageItemsInGroupByItemId(itemId);
      assertNotNull(result);
      assertEquals(0, result.size());
      verify(householdService, times(1)).getGroupIdForCurrentUser();
      verify(storageItemRepo, times(1)).getSharedStorageItemsInGroupByItemId(groupId, itemId);
      verify(householdService, times(0)).getHouseholdNameById(anyLong());
    }
  }
  /**
   * Test for addStorageItem method with valid item.
   * This test verifies that the method successfully adds a valid storage item.
   */
  @Test
  void addStorageItem_shouldAddValidItem() {
    // Setup
    int itemId = 101;
    int householdId = 1;
    StorageItem itemToAdd = createStorageItem(0, itemId, householdId, 5,
            LocalDateTime.now().plusDays(10));
    StorageItem addedItem = createStorageItem(1, itemId, householdId, 5,
            LocalDateTime.now().plusDays(10));

    when(itemRepo.findById(itemId)).thenReturn(Optional.of(new Item()));
    when(storageItemRepo.add(any(StorageItem.class))).thenReturn(addedItem);

    // Execute
    StorageItem result = storageItemService.addStorageItem(itemToAdd);

    // Verify
    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals(itemId, result.getItemId());
    assertEquals(householdId, result.getHouseholdId());
    verify(itemRepo, times(1)).findById(itemId);
    verify(storageItemRepo, times(1)).add(itemToAdd);
  }

  /**
   * Test for addStorageItem method with invalid item (null expiration date).
   * This test verifies that the method throws an exception when a storage item
   * with a null expiration date is provided.
   */
  @Test
  void addStorageItem_shouldThrowException_whenExpirationDateIsNull() {
    // Setup
    int itemId = 101;
    int householdId = 1;
    StorageItem invalidItem = createStorageItem(0, itemId, householdId, 5, null);

    // Execute and verify
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            storageItemService.addStorageItem(invalidItem)
    );

    assertEquals("Expiration date cannot be null", exception.getMessage());
    verify(itemRepo, never()).findById(anyInt());
    verify(storageItemRepo, never()).add(any());
  }

  /**
   * Test for addStorageItem method with non-existent item.
   * This test verifies that the method throws an exception when the referenced item does not exist.
   */
  @Test
  void addStorageItem_shouldThrowException_whenItemDoesNotExist() {
    // Setup
    int itemId = 999;
    int householdId = 1;
    StorageItem itemToAdd = createStorageItem(0, itemId, householdId, 5,
            LocalDateTime.now().plusDays(10));

    when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

    // Execute and verify
    RuntimeException exception = assertThrows(RuntimeException.class, () ->
            storageItemService.addStorageItem(itemToAdd)
    );

    assertEquals("Item not found with id: " + itemId, exception.getMessage());
    verify(itemRepo, times(1)).findById(itemId);
    verify(storageItemRepo, never()).add(any());
  }

  /**
   * Test for updateStorageItem method.
   * This test verifies that the method successfully updates an existing storage item.
   */
  @Test
  void updateStorageItem_shouldUpdateExistingItem() {
    // Setup
    int itemId = 101;
    int storageItemId = 1;
    int householdId = 1;
    StorageItem itemToUpdate = createStorageItem(0, itemId, householdId, 10,
            LocalDateTime.now().plusDays(15));
    StorageItem updatedItem = createStorageItem(storageItemId, itemId, householdId, 10,
            LocalDateTime.now().plusDays(15));

    when(storageItemRepo.findById(storageItemId, householdId)).thenReturn(Optional.of(new StorageItem()));
    when(itemRepo.findById(itemId)).thenReturn(Optional.of(new Item()));
    when(storageItemRepo.update(any(StorageItem.class))).thenReturn(updatedItem);

    // Execute
    StorageItem result = storageItemService.updateStorageItem(storageItemId, householdId, itemToUpdate);

    // Verify
    assertNotNull(result);
    assertEquals(storageItemId, result.getId());
    assertEquals(itemId, result.getItemId());
    assertEquals(householdId, result.getHouseholdId());
    assertEquals(10, result.getQuantity());
    verify(storageItemRepo, times(1)).findById(storageItemId, householdId);
    verify(itemRepo, times(1)).findById(itemId);
    verify(storageItemRepo, times(1)).update(any(StorageItem.class));
  }

  /**
   * Test for deleteStorageItem method.
   * This test verifies that the method successfully deletes an existing storage item.
   */
  @Test
  void deleteStorageItem_shouldDeleteExistingItem() {
    // Setup
    int storageItemId = 1;
    int householdId = 1;

    when(storageItemRepo.findById(storageItemId, householdId)).thenReturn(Optional.of(new StorageItem()));

    // Execute
    storageItemService.deleteStorageItem(storageItemId, householdId);

    // Verify
    verify(storageItemRepo, times(1)).findById(storageItemId, householdId);
    verify(storageItemRepo, times(1)).deleteById(storageItemId, householdId);
  }

  /**
   * Test for deleteStorageItem method when the item does not exist.
   * This test verifies that the method throws an exception when trying to delete a non-existent storage item.
   */
  @Test
  void deleteStorageItem_shouldThrowException_whenItemDoesNotExist() {
    // Setup
    int storageItemId = 999;
    int householdId = 1;

    when(storageItemRepo.findById(storageItemId, householdId)).thenReturn(Optional.empty());

    // Execute and verify
    RuntimeException exception = assertThrows(RuntimeException.class, () ->
            storageItemService.deleteStorageItem(storageItemId, householdId)
    );

    assertEquals("Storage item not found with id: " + storageItemId + " in household: " + householdId,
            exception.getMessage());
    verify(storageItemRepo, times(1)).findById(storageItemId, householdId);
    verify(storageItemRepo, never()).deleteById(anyInt(), anyInt());
  }

  /**
   * Test for getExpiringStorageItems method.
   * This test verifies that the method returns storage items that will expire within the specified number of days.
   */
  @Test
  void getExpiringStorageItems_shouldReturnItemsExpiringWithinDays() {
    // Setup
    int days = 7;
    int householdId = 1;
    List<StorageItem> expiringItems = Arrays.asList(
            createStorageItem(1, 101, householdId, 5, LocalDateTime.now().plusDays(3)),
            createStorageItem(2, 102, householdId, 3, LocalDateTime.now().plusDays(5))
    );

    when(storageItemRepo.findExpiringItems(days, householdId)).thenReturn(expiringItems);

    // Execute
    List<StorageItem> result = storageItemService.getExpiringStorageItems(days, householdId);

    // Verify
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(storageItemRepo, times(1)).findExpiringItems(days, householdId);
  }

  /**
   * Test for convertToStorageItemResponse method.
   * This test verifies that the method correctly converts a StorageItem entity to a StorageItemResponse DTO.
   */
  @Test
  void convertToStorageItemResponse_shouldConvertWithItemDetails() {
    // Setup
    int itemId = 101;
    int storageItemId = 1;
    int householdId = 1;
    StorageItem storageItem = createStorageItem(storageItemId, itemId, householdId, 5,
            LocalDateTime.now().plusDays(10));
    Item item = new Item(itemId, "Test Item", "units", 100, ItemType.FOOD);

    when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

    // Execute
    StorageItemResponse result = storageItemService.convertToStorageItemResponse(storageItem);

    // Verify
    assertNotNull(result);
    assertEquals(storageItemId, result.getId());
    assertEquals(itemId, result.getItemId());
    assertEquals(householdId, result.getHouseholdId());
    assertEquals(5, result.getQuantity());

    assertNotNull(result.getItem());
    assertEquals(itemId, result.getItem().getId());
    assertEquals("Test Item", result.getItem().getName());
    assertEquals("units", result.getItem().getUnit());
    assertEquals(100, result.getItem().getCalories());
    assertEquals(ItemType.FOOD, result.getItem().getType());

    verify(itemRepo, times(1)).findById(itemId);
  }

  /**
   * Test for getAggregatedStorageItems method.
   * This test verifies that the method correctly aggregates storage items by item ID.
   */
  @Test
  void getAggregatedStorageItems_shouldAggregateByItemId() {
    // Setup
    int householdId = 1;
    int itemId1 = 101;
    int itemId2 = 102;

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime earlier = now.minusDays(2);
    LocalDateTime later = now.plusDays(5);

    List<StorageItem> allItems = Arrays.asList(
            createStorageItem(1, itemId1, householdId, 5, later),
            createStorageItem(2, itemId1, householdId, 3, earlier),
            createStorageItem(3, itemId2, householdId, 2, now)
    );

    Item item1 = new Item(itemId1, "Item 1", "units", 100, ItemType.FOOD);
    Item item2 = new Item(itemId2, "Item 2", "units", 200, ItemType.DRINK);

    when(storageItemRepo.getAllStorageItems(householdId)).thenReturn(allItems);
    when(itemRepo.findById(itemId1)).thenReturn(Optional.of(item1));
    when(itemRepo.findById(itemId2)).thenReturn(Optional.of(item2));

    // Execute
    List<AggregatedStorageItemResponse> result = storageItemService.getAggregatedStorageItems(householdId);

    // Verify
    assertNotNull(result);
    assertEquals(2, result.size());

    // Find the aggregated items for each item ID
    AggregatedStorageItemResponse agg1 = result.stream()
            .filter(r -> r.getItemId() == itemId1)
            .findFirst()
            .orElse(null);

    AggregatedStorageItemResponse agg2 = result.stream()
            .filter(r -> r.getItemId() == itemId2)
            .findFirst()
            .orElse(null);

    assertNotNull(agg1);
    assertEquals(itemId1, agg1.getItemId());
    assertEquals(8, agg1.getTotalQuantity()); // 5 + 3
    assertEquals(earlier, agg1.getEarliestExpirationDate()); // The earlier date
    assertNotNull(agg1.getItem());
    assertEquals("Item 1", agg1.getItem().getName());

    assertNotNull(agg2);
    assertEquals(itemId2, agg2.getItemId());
    assertEquals(2, agg2.getTotalQuantity());
    assertEquals(now, agg2.getEarliestExpirationDate());
    assertNotNull(agg2.getItem());
    assertEquals("Item 2", agg2.getItem().getName());

    verify(storageItemRepo, times(1)).getAllStorageItems(householdId);
    verify(itemRepo, times(1)).findById(itemId1);
    verify(itemRepo, times(1)).findById(itemId2);
  }

  /**
   * Test for getFilteredAndSortedAggregatedItems method.
   * This test verifies that the method correctly filters and sorts aggregated storage items.
   */
  @Test
  void getFilteredAndSortedAggregatedItems_shouldFilterByItemTypeAndSort() {
    // Setup
    int householdId = 1;
    List<ItemType> filterTypes = Collections.singletonList(ItemType.FOOD);
    String sortBy = "quantity";
    String sortDirection = "desc";

    // Create mock aggregated responses
    AggregatedStorageItemResponse food1 = createAggregatedResponse(
            101, new ItemResponse(101, "Food 1", "units", 100, ItemType.FOOD), 5,
            LocalDateTime.now().plusDays(10));

    AggregatedStorageItemResponse food2 = createAggregatedResponse(
            102, new ItemResponse(102, "Food 2", "units", 200, ItemType.FOOD), 10,
            LocalDateTime.now().plusDays(5));

    AggregatedStorageItemResponse drink = createAggregatedResponse(
            103, new ItemResponse(103, "Drink", "ml", 50, ItemType.DRINK), 7,
            LocalDateTime.now().plusDays(3));

    List<AggregatedStorageItemResponse> allAggregated = Arrays.asList(food1, food2, drink);

    // Mock service method that's called by the method under test
    StorageItemService spyService = spy(storageItemService);
    doReturn(allAggregated).when(spyService).getAggregatedStorageItems(householdId, null, null);

    // Execute
    List<AggregatedStorageItemResponse> result = spyService.getFilteredAndSortedAggregatedItems(
            householdId, filterTypes, sortBy, sortDirection
    );

    // Verify
    assertNotNull(result);
    assertEquals(2, result.size()); // Only FOOD items should be included

    // Check sorting - should be by quantity in descending order
    assertEquals("Food 2", result.get(0).getItem().getName()); // 10 quantity
    assertEquals("Food 1", result.get(1).getItem().getName()); // 5 quantity

    verify(spyService, times(1)).getAggregatedStorageItems(householdId, null, null);
  }

  /**
   * Test for searchAggregatedStorageItems method.
   * This test verifies that the method correctly searches for storage items by name.
   */
  @Test
  void searchAggregatedStorageItems_shouldFilterByNameAndType() {
    // Setup
    int householdId = 1;
    String searchTerm = "apple";
    List<ItemType> filterTypes = Collections.singletonList(ItemType.FOOD);

    // Create mock aggregated responses
    AggregatedStorageItemResponse appleJuice = createAggregatedResponse(
            101, new ItemResponse(101, "Apple Juice", "ml", 50, ItemType.DRINK), 5,
            LocalDateTime.now().plusDays(10));

    AggregatedStorageItemResponse apples = createAggregatedResponse(
            102, new ItemResponse(102, "Apples", "units", 80, ItemType.FOOD), 10,
            LocalDateTime.now().plusDays(5));

    AggregatedStorageItemResponse bread = createAggregatedResponse(
            103, new ItemResponse(103, "Bread", "slices", 100, ItemType.FOOD), 7,
            LocalDateTime.now().plusDays(3));

    List<AggregatedStorageItemResponse> allAggregated = Arrays.asList(appleJuice, apples, bread);

    // Mock service method that's called by the method under test
    StorageItemService spyService = spy(storageItemService);
    doReturn(allAggregated).when(spyService).getAggregatedStorageItems(householdId);

    // Execute
    List<AggregatedStorageItemResponse> result = spyService.searchAggregatedStorageItems(
            householdId, searchTerm, filterTypes, null, null
    );

    // Verify
    assertNotNull(result);
    assertEquals(1, result.size()); // Only "Apples" matches both the search term and type filter
    assertEquals("Apples", result.get(0).getItem().getName());

    verify(spyService, times(1)).getAggregatedStorageItems(householdId);
  }

  // Helper methods

  private StorageItem createStorageItem(int id, int itemId, int householdId, int quantity,
                                        LocalDateTime expirationDate) {
    StorageItem item = new StorageItem();
    item.setId(id);
    item.setItemId(itemId);
    item.setHouseholdId(householdId);
    item.setQuantity(quantity);
    item.setExpirationDate(expirationDate);
    return item;
  }

  private AggregatedStorageItemResponse createAggregatedResponse(int itemId, ItemResponse item,
                                                                 int totalQuantity,
                                                                 LocalDateTime earliestExpirationDate) {
    return new AggregatedStorageItemResponse(itemId, item, totalQuantity,
            earliestExpirationDate);
  }
}
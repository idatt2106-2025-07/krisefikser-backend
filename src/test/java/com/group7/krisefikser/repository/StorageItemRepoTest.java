package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.StorageItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is a test class for the StorageItemRepo.
 * It tests the functionality of the methods in the StorageItemRepo class.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StorageItemRepoTest {
  @Autowired
  private StorageItemRepo storageItemRepo;

  /**
   * This method tests the getAllStorageItems method in the StorageItemRepo class.
   * It retrieves all storage items for a specific household from the database and checks
   * if the list is not null, and that items match the expected household.
   */
  @Test
  void getAllStorageItems() {
    int householdId = 1;
    List<StorageItem> items = storageItemRepo.getAllStorageItems(householdId);

    assertNotNull(items);
    assertFalse(items.isEmpty());

    // Verify all items belong to the specified household
    for (StorageItem item : items) {
      assertEquals(householdId, item.getHouseholdId());
    }
  }

  /**
   * This method tests the findById method in the StorageItemRepo class.
   * It retrieves a storage item based on its id and household id from the database
   * and checks if the optional is not empty and the attributes of the item are correct.
   */
  @Test
  void findById() {
    int itemId = 1;
    int householdId = 1;

    Optional<StorageItem> itemOptional = storageItemRepo.findById(itemId, householdId);

    assertTrue(itemOptional.isPresent());
    StorageItem item = itemOptional.get();
    assertEquals(itemId, item.getId());
    assertEquals(householdId, item.getHouseholdId());
    assertNotNull(item.getExpirationDate());
    assertTrue(item.getQuantity() >= 0);
  }

  /**
   * This method tests the findById method in the StorageItemRepo class with a non-existent id.
   * It verifies that an empty optional is returned when no storage item with the given id exists.
   */
  @Test
  void findByIdNonExistent() {
    int nonExistentId = 999;
    int householdId = 1;

    Optional<StorageItem> itemOptional = storageItemRepo.findById(nonExistentId, householdId);

    assertTrue(itemOptional.isEmpty());
  }

  /**
   * This method tests the findByItemId method in the StorageItemRepo class.
   * It retrieves storage items based on their item id and household id from the database
   * and checks if the list is not null and the attributes of the items are as expected.
   */
  @Test
  void findByItemId() {
    int itemId = 1; // Assuming item 1 exists and has storage items
    int householdId = 1;

    List<StorageItem> items = storageItemRepo.findByItemId(itemId, householdId);

    assertNotNull(items);
    assertFalse(items.isEmpty());

    // Check that all returned items have the correct item ID and household ID
    for (StorageItem item : items) {
      assertEquals(itemId, item.getItemId());
      assertEquals(householdId, item.getHouseholdId());
    }
  }

  /**
   * This method tests the add method in the StorageItemRepo class.
   * It adds a new storage item to the database and checks if the item is correctly added
   * with a generated id and all the attributes are correctly set.
   */
  @Test
  void add() {
    // Create a new storage item
    StorageItem newItem = new StorageItem();
    newItem.setExpirationDate(LocalDateTime.now().plusDays(10));
    newItem.setQuantity(5);
    newItem.setHouseholdId(1);
    newItem.setItemId(1); // Assuming item ID 1 exists

    StorageItem addedItem = storageItemRepo.add(newItem);

    assertNotNull(addedItem);
    assertTrue(addedItem.getId() > 0);
    assertEquals(5, addedItem.getQuantity());
    assertEquals(1, addedItem.getHouseholdId());
    assertEquals(1, addedItem.getItemId());
    assertFalse(addedItem.isShared());
    assertNotNull(addedItem.getExpirationDate());

    // Verify the item was actually added to the database
    Optional<StorageItem> retrievedItem = storageItemRepo.findById(addedItem.getId(), addedItem.getHouseholdId());
    assertTrue(retrievedItem.isPresent());
    assertEquals(5, retrievedItem.get().getQuantity());
  }

  /**
   * This method tests the update method in the StorageItemRepo class.
   * It updates an existing storage item in the database and checks if the item
   * is correctly updated with all the attributes correctly modified.
   */
  @Test
  void update() {
    // First, add a new item to update
    StorageItem newItem = new StorageItem();
    newItem.setExpirationDate(LocalDateTime.now().plusDays(10));
    newItem.setQuantity(5);
    newItem.setHouseholdId(1);
    newItem.setItemId(1);

    StorageItem addedItem = storageItemRepo.add(newItem);

    // Now update the item
    addedItem.setQuantity(10);
    addedItem.setExpirationDate(LocalDateTime.now().plusDays(20));

    StorageItem updatedItem = storageItemRepo.update(addedItem);

    assertNotNull(updatedItem);
    assertEquals(addedItem.getId(), updatedItem.getId());
    assertEquals(10, updatedItem.getQuantity());
    assertEquals(addedItem.getHouseholdId(), updatedItem.getHouseholdId());
    assertEquals(addedItem.getItemId(), updatedItem.getItemId());
    assertFalse(updatedItem.isShared());

    // Verify the change in the database
    Optional<StorageItem> retrievedItem = storageItemRepo.findById(updatedItem.getId(), updatedItem.getHouseholdId());
    assertTrue(retrievedItem.isPresent());
    assertEquals(10, retrievedItem.get().getQuantity());
  }

  /**
   * This method tests the update method in the StorageItemRepo class with a non-existent item.
   * It verifies that an exception is thrown when trying to update a storage item that doesn't exist.
   */
  @Test
  void updateNonExistent() {
    StorageItem nonExistentItem = new StorageItem();
    nonExistentItem.setId(999); // Non-existent ID
    nonExistentItem.setExpirationDate(LocalDateTime.now().plusDays(10));
    nonExistentItem.setQuantity(5);
    nonExistentItem.setHouseholdId(1);
    nonExistentItem.setItemId(1);

    assertThrows(EmptyResultDataAccessException.class, () -> {
      storageItemRepo.update(nonExistentItem);
    });
  }

  /**
   * This method tests the deleteById method in the StorageItemRepo class.
   * It deletes a storage item from the database and checks if the deletion was successful
   * and the item no longer exists in the database.
   */
  @Test
  void deleteById() {
    // First, add a new item to delete
    StorageItem newItem = new StorageItem();
    newItem.setExpirationDate(LocalDateTime.now().plusDays(10));
    newItem.setQuantity(5);
    newItem.setHouseholdId(1);
    newItem.setItemId(1);

    StorageItem addedItem = storageItemRepo.add(newItem);

    // Verify the item exists
    Optional<StorageItem> existingItemOpt = storageItemRepo.findById(addedItem.getId(), addedItem.getHouseholdId());
    assertTrue(existingItemOpt.isPresent());

    // Delete the item
    boolean result = storageItemRepo.deleteById(addedItem.getId(), addedItem.getHouseholdId());

    // Verify deletion was successful
    assertTrue(result);

    // Verify the item is no longer in the database
    Optional<StorageItem> deletedItemOpt = storageItemRepo.findById(addedItem.getId(), addedItem.getHouseholdId());
    assertTrue(deletedItemOpt.isEmpty());
  }

  /**
   * This method tests the deleteByHouseholdId method in the StorageItemRepo class.
   * It deletes all storage items for a specific household and verifies that they're all deleted.
   */
  @Test
  void deleteByHouseholdId() {
    // Use an existing household ID (1 is typically available in test data)
    int existingHouseholdId = 1;

    // Count the initial items for this household
    List<StorageItem> initialItems = storageItemRepo.getAllStorageItems(existingHouseholdId);
    int initialCount = initialItems.size();

    // Add a couple of new items to the existing household
    StorageItem item1 = new StorageItem();
    item1.setExpirationDate(LocalDateTime.now().plusDays(10));
    item1.setQuantity(5);
    item1.setHouseholdId(existingHouseholdId);
    item1.setItemId(1); // Assuming item ID 1 exists

    StorageItem item2 = new StorageItem();
    item2.setExpirationDate(LocalDateTime.now().plusDays(15));
    item2.setQuantity(3);
    item2.setHouseholdId(existingHouseholdId);
    item2.setItemId(2); // Assuming item ID 2 exists

    storageItemRepo.add(item1);
    storageItemRepo.add(item2);

    // Verify items were added
    List<StorageItem> itemsAfterAdd = storageItemRepo.getAllStorageItems(existingHouseholdId);
    assertEquals(initialCount + 2, itemsAfterAdd.size());

    // Delete all items for this household
    int deletedCount = storageItemRepo.deleteByHouseholdId(existingHouseholdId);

    // Verify correct number of items were deleted
    assertEquals(initialCount + 2, deletedCount);

    // Verify no items remain for this household
    List<StorageItem> itemsAfterDelete = storageItemRepo.getAllStorageItems(existingHouseholdId);
    assertTrue(itemsAfterDelete.isEmpty());
  }

  /**
   * This method tests the findExpiringItems method in the StorageItemRepo class.
   * It adds items with different expiration dates and verifies that only items
   * expiring within the specified days are returned.
   */
  @Test
  void findExpiringItems() {
    // Use an existing household ID
    int existingHouseholdId = 1;

    // First, clean up any existing items for this test
    storageItemRepo.deleteByHouseholdId(existingHouseholdId);

    // Add items with different expiration dates
    StorageItem expiringSoon = new StorageItem();
    expiringSoon.setExpirationDate(LocalDateTime.now().plusDays(2));
    expiringSoon.setQuantity(5);
    expiringSoon.setHouseholdId(existingHouseholdId);
    expiringSoon.setItemId(1);

    StorageItem expiringLater = new StorageItem();
    expiringLater.setExpirationDate(LocalDateTime.now().plusDays(8));
    expiringLater.setQuantity(3);
    expiringLater.setHouseholdId(existingHouseholdId);
    expiringLater.setItemId(2);

    StorageItem farFromExpiry = new StorageItem();
    farFromExpiry.setExpirationDate(LocalDateTime.now().plusDays(20));
    farFromExpiry.setQuantity(2);
    farFromExpiry.setHouseholdId(existingHouseholdId);
    farFromExpiry.setItemId(3);

    storageItemRepo.add(expiringSoon);
    storageItemRepo.add(expiringLater);
    storageItemRepo.add(farFromExpiry);

    // Find items expiring within 5 days
    List<StorageItem> expiringItems = storageItemRepo.findExpiringItems(5, existingHouseholdId);

    // Verify only the item expiring soon is returned
    assertEquals(1, expiringItems.size());
    assertEquals(expiringSoon.getId(), expiringItems.get(0).getId());

    // Find items expiring within 10 days
    List<StorageItem> expiringItems10Days = storageItemRepo.findExpiringItems(10, existingHouseholdId);

    // Verify both expiring items are returned
    assertEquals(2, expiringItems10Days.size());

    // Clean up test data
    storageItemRepo.deleteByHouseholdId(existingHouseholdId);
  }

  /**
   * This method tests the findByItemTypes method in the StorageItemRepo class.
   * It verifies that items are correctly filtered by item type.
   */
  @Test
  void findByItemTypes() {
    int householdId = 1;

    // Test with FOOD item type
    List<String> foodTypes = Arrays.asList("FOOD");
    List<StorageItem> foodItems = storageItemRepo.findByItemTypes(householdId, foodTypes);

    // Verify items were returned (assuming test data contains food items)
    assertNotNull(foodItems);

    // Test with multiple types
    List<String> multipleTypes = Arrays.asList("FOOD", "DRINK");
    List<StorageItem> multipleTypeItems = storageItemRepo.findByItemTypes(householdId, multipleTypes);

    // Verify items were returned
    assertNotNull(multipleTypeItems);

    // Verify that combining types returns more (or same) items than just one type
    assertTrue(multipleTypeItems.size() >= foodItems.size());

    // Test with empty types list - should return all items
    List<StorageItem> allItems = storageItemRepo.findByItemTypes(householdId, null);

    // Verify items were returned
    assertNotNull(allItems);
    assertEquals(storageItemRepo.getAllStorageItems(householdId).size(), allItems.size());
  }
}
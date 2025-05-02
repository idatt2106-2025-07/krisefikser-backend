package com.group7.krisefikser.service;

import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.Item;
import com.group7.krisefikser.repository.ItemRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ItemService class.
 * This class tests the methods of the ItemService class
 * to ensure they behave as expected.
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepo itemRepo;

    @InjectMocks
    private ItemService itemService;

    /**
     * Test for getAllItems method.
     * This test verifies that the method returns all items from the repository.
     */
    @Test
    void getAllItems_shouldReturnAllItems() {
        List<Item> mockItems = Arrays.asList(
            new Item(1, "Bottled Water", "L", 0, ItemType.DRINK),
            new Item(2, "Canned Beans", "g", 120, ItemType.FOOD)
        );
        when(itemRepo.getAllItems()).thenReturn(mockItems);

        List<Item> result = itemService.getAllItems();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Bottled Water", result.get(0).getName());
        assertEquals("Canned Beans", result.get(1).getName());
        verify(itemRepo, times(1)).getAllItems();
    }

    /**
     * Test for getAllItems method when the repository returns an empty list.
     * This test verifies that the method returns an empty list when no items are found.
     */
    @Test
    void getAllItems_shouldReturnEmptyList_whenNoItemsExist() {
        when(itemRepo.getAllItems()).thenReturn(Collections.emptyList());

        List<Item> result = itemService.getAllItems();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepo, times(1)).getAllItems();
    }

    /**
     * Test for getItemById method.
     * This test verifies that the method returns the correct item when it exists.
     */
    @Test
    void getItemById_shouldReturnItem_whenItemExists() {
        Item mockItem = new Item(1, "Bottled Water", "L", 0, ItemType.DRINK);
        when(itemRepo.findById(1)).thenReturn(Optional.of(mockItem));

        Item result = itemService.getItemById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Bottled Water", result.getName());
        assertEquals("L", result.getUnit());
        assertEquals(0, result.getCalories());
        assertEquals(ItemType.DRINK, result.getType());
        verify(itemRepo, times(1)).findById(1);
    }

    /**
     * Test for getItemById method when the item does not exist.
     * This test verifies that the method throws an exception when the item is not found.
     */
    @Test
    void getItemById_shouldThrowException_whenItemDoesNotExist() {
        when(itemRepo.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            itemService.getItemById(999)
        );
        assertEquals("Item not found with id: 999", exception.getMessage());
        verify(itemRepo, times(1)).findById(999);
    }

    /**
     * Test for addItem method.
     * This test verifies that the method successfully adds a valid item.
     */
    @Test
    void addItem_shouldAddValidItem() {
        Item itemToAdd = new Item(0, "New Item", "unit", 100, ItemType.DRINK);
        Item addedItem = new Item(10, "New Item", "unit", 100, ItemType.DRINK);
        when(itemRepo.add(any(Item.class))).thenReturn(addedItem);

        Item result = itemService.addItem(itemToAdd);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("New Item", result.getName());
        verify(itemRepo, times(1)).add(itemToAdd);
    }

    /**
     * Test for addItem method with invalid item (null name).
     * This test verifies that the method throws an exception when an item with a null name is provided.
     */
    @Test
    void addItem_shouldThrowException_whenNameIsNull() {
        Item invalidItem = new Item(0, null, "unit", 100, ItemType.DRINK);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            itemService.addItem(invalidItem)
        );
        assertEquals("Item name cannot be empty", exception.getMessage());
        verify(itemRepo, never()).add(any());
    }

    /**
     * Test for addItem method with invalid item (empty name).
     * This test verifies that the method throws an exception when an item with an empty name is provided.
     */
    @Test
    void addItem_shouldThrowException_whenNameIsEmpty() {
        Item invalidItem = new Item(0, "  ", "unit", 100, ItemType.DRINK);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            itemService.addItem(invalidItem)
        );
        assertEquals("Item name cannot be empty", exception.getMessage());
        verify(itemRepo, never()).add(any());
    }

    /**
     * Test for addItem method with invalid item (null unit).
     * This test verifies that the method throws an exception when an item with a null unit is provided.
     */
    @Test
    void addItem_shouldThrowException_whenUnitIsNull() {
        Item invalidItem = new Item(0, "New Item", null, 100, ItemType.DRINK);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            itemService.addItem(invalidItem)
        );
        assertEquals("Item unit cannot be empty", exception.getMessage());
        verify(itemRepo, never()).add(any());
    }

    /**
     * Test for addItem method with invalid item (empty unit).
     * This test verifies that the method throws an exception when an item with an empty unit is provided.
     */
    @Test
    void addItem_shouldThrowException_whenUnitIsEmpty() {
        Item invalidItem = new Item(0, "New Item", "", 100, ItemType.DRINK);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            itemService.addItem(invalidItem)
        );
        assertEquals("Item unit cannot be empty", exception.getMessage());
        verify(itemRepo, never()).add(any());
    }

    /**
     * Test for addItem method with invalid item (negative calories).
     * This test verifies that the method throws an exception when an item with negative calories is provided.
     */
    @Test
    void addItem_shouldThrowException_whenCaloriesIsNegative() {
        Item invalidItem = new Item(0, "New Item", "unit", -10, ItemType.DRINK);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            itemService.addItem(invalidItem)
        );
        assertEquals("Calories cannot be negative", exception.getMessage());
        verify(itemRepo, never()).add(any());
    }

    /**
     * Test for addItem method with invalid item (null type).
     * This test verifies that the method throws an exception when an item with a null type is provided.
     */
    @Test
    void addItem_shouldThrowException_whenTypeIsNull() {
        Item invalidItem = new Item(0, "New Item", "unit", 100, null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            itemService.addItem(invalidItem)
        );
        assertEquals("Item type must be specified", exception.getMessage());
        verify(itemRepo, never()).add(any());
    }

    /**
     * Test for updateItem method.
     * This test verifies that the method successfully updates an existing item.
     */
    @Test
    void updateItem_shouldUpdateExistingItem() {
        int itemId = 1;
        Item itemToUpdate = new Item(0, "Updated Item", "kg", 200, ItemType.FOOD);
        Item updatedItem = new Item(itemId, "Updated Item", "kg", 200, ItemType.FOOD);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(itemRepo.update(any(Item.class))).thenReturn(updatedItem);

        Item result = itemService.updateItem(itemId, itemToUpdate);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals("Updated Item", result.getName());
        assertEquals("kg", result.getUnit());
        assertEquals(200, result.getCalories());
        assertEquals(ItemType.FOOD, result.getType());

        verify(itemRepo, times(1)).findById(itemId);
        verify(itemRepo, times(1)).update(any(Item.class));
    }

    /**
     * Test for updateItem method when the item does not exist.
     * This test verifies that the method throws an exception when trying to update a non-existent item.
     */
    @Test
    void updateItem_shouldThrowException_whenItemDoesNotExist() {
        int itemId = 999;
        Item itemToUpdate = new Item(0, "Updated Item", "kg", 200, ItemType.FOOD);

        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            itemService.updateItem(itemId, itemToUpdate)
        );

        assertEquals("Item not found with id: 999", exception.getMessage());
        verify(itemRepo, times(1)).findById(itemId);
        verify(itemRepo, never()).update(any());
    }

    /**
     * Test for updateItem method with invalid item data.
     * This test verifies that validation is performed during updates.
     */
    @Test
    void updateItem_shouldThrowException_whenItemDataIsInvalid() {
        int itemId = 1;
        Item itemToUpdate = new Item(0, "", "kg", 200, ItemType.FOOD);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(new Item()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            itemService.updateItem(itemId, itemToUpdate)
        );

        assertEquals("Item name cannot be empty", exception.getMessage());
        verify(itemRepo, times(1)).findById(itemId);
        verify(itemRepo, never()).update(any());
    }

    /**
     * Test for deleteItem method.
     * This test verifies that the method successfully deletes an existing item.
     */
    @Test
    void deleteItem_shouldDeleteExistingItem() {
        int itemId = 1;
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(itemRepo.deleteById(itemId)).thenReturn(true);

        itemService.deleteItem(itemId);

        verify(itemRepo, times(1)).findById(itemId);
        verify(itemRepo, times(1)).deleteById(itemId);
    }

    /**
     * Test for deleteItem method when the item does not exist.
     * This test verifies that the method throws an exception when trying to delete a non-existent item.
     */
    @Test
    void deleteItem_shouldThrowException_whenItemDoesNotExist() {
        int itemId = 999;
        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            itemService.deleteItem(itemId)
        );

        assertEquals("Item not found with id: 999", exception.getMessage());
        verify(itemRepo, times(1)).findById(itemId);
        verify(itemRepo, never()).deleteById(anyInt());
    }

    /**
     * Test for itemExists method when the item exists.
     * This test verifies that the method returns true when the item exists.
     */
    @Test
    void itemExists_shouldReturnTrue_whenItemExists() {
        int itemId = 1;
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(new Item()));

        boolean result = itemService.itemExists(itemId);

        assertTrue(result);
        verify(itemRepo, times(1)).findById(itemId);
    }

    /**
     * Test for itemExists method when the item does not exist.
     * This test verifies that the method returns false when the item does not exist.
     */
    @Test
    void itemExists_shouldReturnFalse_whenItemDoesNotExist() {
        int itemId = 999;
        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        boolean result = itemService.itemExists(itemId);

        assertFalse(result);
        verify(itemRepo, times(1)).findById(itemId);
    }

    /**
     * Test for getItemsByTypes method.
     * This test verifies that the method returns items of multiple specified types.
     */
    @Test
    void getItemsByTypes_shouldReturnItemsOfSpecifiedTypes() {
        Item drink1 = new Item(1, "Water", "liter", 0, ItemType.DRINK);
        Item drink2 = new Item(2, "Juice", "ml", 45, ItemType.DRINK);
        Item food1 = new Item(3, "Bread", "piece", 265, ItemType.FOOD);
        Item accessory1 = new Item(4, "Flashlight", "piece", 0, ItemType.ACCESSORIES);

        List<Item> allItems = Arrays.asList(drink1, drink2, food1, accessory1);

        when(itemRepo.getAllItems()).thenReturn(allItems);

        List<ItemType> typesToFilter = Arrays.asList(ItemType.DRINK, ItemType.FOOD);
        List<Item> result = itemService.getItemsByTypes(typesToFilter);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(drink1));
        assertTrue(result.contains(drink2));
        assertTrue(result.contains(food1));
        assertFalse(result.contains(accessory1));
        verify(itemRepo, times(1)).getAllItems();
    }

    /**
     * Test for getItemsByTypes method with empty type list.
     * This test verifies that the method returns all items when no types are specified.
     */
    @Test
    void getItemsByTypes_shouldReturnAllItems_whenTypesListIsEmpty() {
        List<Item> allItems = Arrays.asList(
            new Item(1, "Water", "liter", 0, ItemType.DRINK),
            new Item(2, "Bread", "piece", 265, ItemType.FOOD),
            new Item(3, "Flashlight", "piece", 0, ItemType.ACCESSORIES)
        );

        when(itemRepo.getAllItems()).thenReturn(allItems);

        List<Item> result = itemService.getItemsByTypes(Collections.emptyList());

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(allItems, result);
        verify(itemRepo, times(1)).getAllItems();
    }

    /**
     * Test for getSortedItems method.
     * This test verifies that items are sorted by name in ascending order.
     */
    @Test
    void getSortedItems_shouldSortByNameAscending() {
        // Setup test data
        Item item1 = new Item(1, "Water", "liter", 0, ItemType.DRINK);
        Item item2 = new Item(2, "Bread", "piece", 265, ItemType.FOOD);
        Item item3 = new Item(3, "Apple", "piece", 52, ItemType.FOOD);

        List<Item> unsortedItems = Arrays.asList(item1, item2, item3);

        when(itemRepo.getAllItems()).thenReturn(unsortedItems);

        List<Item> result = itemService.getSortedItems("name", "asc");

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Apple", result.get(0).getName());
        assertEquals("Bread", result.get(1).getName());
        assertEquals("Water", result.get(2).getName());
        verify(itemRepo, times(1)).getAllItems();
    }

    /**
     * Test for getSortedItems method.
     * This test verifies that items are sorted by calories in descending order.
     */
    @Test
    void getSortedItems_shouldSortByCaloriesDescending() {
        Item item1 = new Item(1, "Water", "liter", 0, ItemType.DRINK);
        Item item2 = new Item(2, "Bread", "piece", 265, ItemType.FOOD);
        Item item3 = new Item(3, "Apple", "piece", 52, ItemType.FOOD);

        List<Item> unsortedItems = Arrays.asList(item1, item2, item3);

        when(itemRepo.getAllItems()).thenReturn(unsortedItems);

        List<Item> result = itemService.getSortedItems("calories", "desc");

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Bread", result.get(0).getName());
        assertEquals("Apple", result.get(1).getName());
        assertEquals("Water", result.get(2).getName());
        verify(itemRepo, times(1)).getAllItems();
    }
}
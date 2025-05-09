package com.group7.krisefikser.repository;

import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.item.Item;
import com.group7.krisefikser.repository.item.ItemRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is a test class for the ItemRepo.
 * It tests the functionality of the methods in the ItemRepo class.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRepoTest {
    @Autowired
    private ItemRepo itemRepo;

    /**
     * This method tests the getAllItems method in the ItemRepo class.
     * It retrieves all items from the database and checks if the list is not null,
     * the size of the list is correct, and the attributes of the first item are correct.
     */
    @Test
    void getAllItems() {
        List<Item> items = itemRepo.getAllItems();

        assertNotNull(items);
        assertEquals(10, items.size());
        assertEquals(1, items.get(0).getId());
        assertEquals("Bottled Water", items.get(0).getName());
        assertEquals("L", items.get(0).getUnit());
        assertEquals(0, items.get(0).getCalories());
        assertEquals(ItemType.DRINK, items.get(0).getType());
    }

    /**
     * This method tests the findById method in the ItemRepo class.
     * It retrieves an item based on its id from the database and checks
     * if the optional is not empty and the attributes of the item are correct.
     */
    @Test
    void findById() {
        Optional<Item> itemOptional = itemRepo.findById(2);

        assertTrue(itemOptional.isPresent());
        Item item = itemOptional.get();
        assertEquals(2, item.getId());
        assertEquals("Canned Beans", item.getName());
        assertEquals("g", item.getUnit());
        assertEquals(120, item.getCalories());
        assertEquals(ItemType.FOOD, item.getType());
    }

    /**
     * This method tests the findById method in the ItemRepo class with a non-existent id.
     * It verifies that an empty optional is returned when no item with the given id exists.
     */
    @Test
    void findByIdNonExistent() {
        Optional<Item> itemOptional = itemRepo.findById(999);
        assertTrue(itemOptional.isEmpty());
    }

    /**
     * This method tests the findByType method in the ItemRepo class.
     * It retrieves items based on their type from the database and checks if the list is not null,
     * the size of the list is correct, and the attributes of the items are as expected.
     */
    @Test
    void findByType() {
        List<Item> foodItems = itemRepo.findByType(ItemType.FOOD);

        assertNotNull(foodItems);
        assertEquals(5, foodItems.size());

        // Check that all returned items are food type
        for (Item item : foodItems) {
            assertEquals(ItemType.FOOD, item.getType());
        }

        // Verify a specific food item
        assertTrue(foodItems.stream().anyMatch(i -> i.getName().equals("Canned Beans")));
    }

    /**
     * This method tests the add method in the ItemRepo class.
     * It adds a new item to the database and checks if the item is correctly added
     * with a generated id and all the attributes are correctly set.
     */
    @Test
    void add() {
        Item newItem = new Item();
        newItem.setName("Test Item");
        newItem.setUnit("piece");
        newItem.setCalories(50);
        newItem.setType(ItemType.ACCESSORIES);

        Item addedItem = itemRepo.add(newItem);

        assertNotNull(addedItem);
        assertTrue(addedItem.getId() > 0);
        assertEquals("Test Item", addedItem.getName());
        assertEquals("piece", addedItem.getUnit());
        assertEquals(50, addedItem.getCalories());
        assertEquals(ItemType.ACCESSORIES, addedItem.getType());

        // Verify the item was actually added to the database
        Optional<Item> retrievedItem = itemRepo.findById(addedItem.getId());
        assertTrue(retrievedItem.isPresent());
        assertEquals("Test Item", retrievedItem.get().getName());
    }

    /**
     * This method tests the update method in the ItemRepo class.
     * It updates an existing item in the database and checks if the item
     * is correctly updated with all the attributes correctly modified.
     */
    @Test
    void update() {
        // First get an existing item
        Optional<Item> existingItemOpt = itemRepo.findById(1);
        assertTrue(existingItemOpt.isPresent());

        Item itemToUpdate = existingItemOpt.get();
        itemToUpdate.setName("Updated Water");
        itemToUpdate.setUnit("ml");
        itemToUpdate.setCalories(10);

        Item updatedItem = itemRepo.update(itemToUpdate);

        assertNotNull(updatedItem);
        assertEquals(1, updatedItem.getId());
        assertEquals("Updated Water", updatedItem.getName());
        assertEquals("ml", updatedItem.getUnit());
        assertEquals(10, updatedItem.getCalories());

        // Verify the change in the database
        Optional<Item> retrievedItem = itemRepo.findById(1);
        assertTrue(retrievedItem.isPresent());
        assertEquals("Updated Water", retrievedItem.get().getName());
    }

    /**
     * This method tests the deleteById method in the ItemRepo class.
     * It deletes an item from the database and checks if the deletion was successful
     * and the item no longer exists in the database.
     */
    @Test
    void deleteById() {
        // First, make sure the item exists
        Optional<Item> existingItemOpt = itemRepo.findById(10);
        assertTrue(existingItemOpt.isPresent());

        // Delete the item
        boolean result = itemRepo.deleteById(10);

        // Verify deletion was successful
        assertTrue(result);

        // Verify the item is no longer in the database
        Optional<Item> deletedItemOpt = itemRepo.findById(10);
        assertTrue(deletedItemOpt.isEmpty());
    }
}
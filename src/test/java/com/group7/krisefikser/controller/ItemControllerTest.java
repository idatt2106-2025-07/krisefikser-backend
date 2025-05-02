package com.group7.krisefikser.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.ItemFilterRequest;
import com.group7.krisefikser.dto.request.ItemRequest;
import com.group7.krisefikser.dto.request.ItemSortRequest;
import com.group7.krisefikser.dto.response.ItemResponse;
import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.Item;
import com.group7.krisefikser.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Test class for the ItemController.
 * This class contains unit tests for the ItemController methods.
 * It uses MockMvc to perform requests and verify responses.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test for the getAllItems method.
     * This test verifies that the method returns a list of items
     * when called.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void getAllItems_shouldReturnOkWithItems_whenServiceReturnsItems() throws Exception {
        List<Item> mockItems = Arrays.asList(
            new Item(1, "Water", "liter", 0, ItemType.DRINK),
            new Item(2, "Bread", "piece", 265, ItemType.FOOD)
        );
        when(itemService.getAllItems()).thenReturn(mockItems);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/items")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        assertEquals(2, actualResponses.size());
        assertEquals(1, actualResponses.get(0).getId());
        assertEquals("Water", actualResponses.get(0).getName());
        assertEquals(ItemType.DRINK, actualResponses.get(0).getType());
        assertEquals(2, actualResponses.get(1).getId());
        assertEquals("Bread", actualResponses.get(1).getName());
        assertEquals(ItemType.FOOD, actualResponses.get(1).getType());
    }

    /**
     * Test for the getAllItems method.
     * This test verifies that the method returns an empty list
     * when the service returns an empty list.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void getAllItems_shouldReturnOkWithEmptyList_whenServiceReturnsEmptyList() throws Exception {
        when(itemService.getAllItems()).thenReturn(Collections.emptyList());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/items")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        assertTrue(actualResponses.isEmpty());
    }

    /**
     * Test for the getItemById method.
     * This test verifies that the method returns an item
     * when called with a valid ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void getItemById_shouldReturnOkWithItem_whenServiceReturnsItem() throws Exception {
        Item mockItem = new Item(1, "Water", "liter", 0, ItemType.DRINK);
        when(itemService.getItemById(1)).thenReturn(mockItem);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ItemResponse actualResponse = objectMapper.readValue(responseContent, ItemResponse.class);
        assertEquals(1, actualResponse.getId());
        assertEquals("Water", actualResponse.getName());
        assertEquals("liter", actualResponse.getUnit());
        assertEquals(0, actualResponse.getCalories());
        assertEquals(ItemType.DRINK, actualResponse.getType());
    }

    /**
     * Test for the getItemById method.
     * This test verifies that the method returns a not found status
     * when the service throws a RuntimeException for non-existent ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void getItemById_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        when(itemService.getItemById(99)).thenThrow(new RuntimeException("Item not found with id: 99"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/items/99")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();
    }

    /**
     * Test for the addItem method.
     * This test verifies that the method returns a created status
     * when a valid item is added.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void addItem_shouldReturnCreatedWithItem_whenValidItemProvided() throws Exception {
        ItemRequest request = new ItemRequest("Water", "liter", 0, ItemType.DRINK);
        Item savedItem = new Item(1, "Water", "liter", 0, ItemType.DRINK);
        when(itemService.addItem(any(Item.class))).thenReturn(savedItem);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ItemResponse actualResponse = objectMapper.readValue(responseContent, ItemResponse.class);
        assertEquals(1, actualResponse.getId());
        assertEquals("Water", actualResponse.getName());
        assertEquals("liter", actualResponse.getUnit());
        assertEquals(0, actualResponse.getCalories());
        assertEquals(ItemType.DRINK, actualResponse.getType());
    }

    /**
     * Test for the addItem method.
     * This test verifies that the method returns a bad request status
     * when invalid item data is provided.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void addItem_shouldReturnBadRequest_whenInvalidItemProvided() throws Exception {
        ItemRequest request = new ItemRequest("", "", -10, null);
        when(itemService.addItem(any(Item.class))).thenThrow(new IllegalArgumentException("Item name cannot be empty"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
    }

    /**
     * Test for the updateItem method.
     * This test verifies that the method returns an ok status
     * when a valid item update is performed.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void updateItem_shouldReturnOkWithUpdatedItem_whenValidUpdatePerformed() throws Exception {
        ItemRequest request = new ItemRequest("Updated Water", "liter", 5, ItemType.DRINK);
        Item updatedItem = new Item(1, "Updated Water", "liter", 5, ItemType.DRINK);
        when(itemService.updateItem(anyInt(), any(Item.class))).thenReturn(updatedItem);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ItemResponse actualResponse = objectMapper.readValue(responseContent, ItemResponse.class);
        assertEquals(1, actualResponse.getId());
        assertEquals("Updated Water", actualResponse.getName());
        assertEquals("liter", actualResponse.getUnit());
        assertEquals(5, actualResponse.getCalories());
        assertEquals(ItemType.DRINK, actualResponse.getType());
    }

    /**
     * Test for the updateItem method.
     * This test verifies that the method returns a not found status
     * when attempting to update a non-existent item.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void updateItem_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        ItemRequest request = new ItemRequest("Updated Water", "liter", 5, ItemType.DRINK);
        when(itemService.updateItem(anyInt(), any(Item.class))).thenThrow(new RuntimeException("Item not found with id: 99"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/items/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();
    }

    /**
     * Test for the updateItem method.
     * This test verifies that the method returns a bad request status
     * when invalid item data is provided for update.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void updateItem_shouldReturnBadRequest_whenInvalidItemProvided() throws Exception {
        ItemRequest request = new ItemRequest("", "", -10, null);
        when(itemService.updateItem(anyInt(), any(Item.class))).thenThrow(new IllegalArgumentException("Item name cannot be empty"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
    }

    /**
     * Test for the deleteItem method.
     * This test verifies that the method returns a no content status
     * when a successful deletion is performed.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void deleteItem_shouldReturnNoContent_whenSuccessfulDeletion() throws Exception {
        doNothing().when(itemService).deleteItem(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andReturn();
    }

    /**
     * Test for the deleteItem method.
     * This test verifies that the method returns a not found status
     * when attempting to delete a non-existent item.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void deleteItem_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        doThrow(new RuntimeException("Item not found with id: 99")).when(itemService).deleteItem(99);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/items/99")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();
    }

    /**
     * Test for the filterItems method with GET mapping.
     * This test verifies that the method returns filtered items
     * when called with valid filter criteria using query parameters.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void filterItems_shouldReturnOkWithFilteredItems_whenValidTypesProvided() throws Exception {
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Water", "liter", 0, ItemType.DRINK),
          new Item(3, "Juice", "ml", 45, ItemType.DRINK)
        );

        List<ItemType> expectedTypes = Collections.singletonList(ItemType.DRINK);

        when(itemService.getItemsByTypes(argThat(
          types -> types.size() == expectedTypes.size() && types.containsAll(expectedTypes))))
          .thenReturn(mockItems);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/items/filter")
            .param("types", "DRINK")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});

        assertEquals(2, actualResponses.size());
        assertEquals(1, actualResponses.get(0).getId());
        assertEquals("Water", actualResponses.get(0).getName());
        assertEquals(ItemType.DRINK, actualResponses.get(0).getType());
    }

    /**
     * Test for the filterItems method with GET mapping.
     * This test verifies that the method handles invalid filter types gracefully
     * when passed as query parameters.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void filterItems_shouldFilterOutInvalidTypes_whenInvalidTypesProvided() throws Exception {
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Water", "liter", 0, ItemType.DRINK),
          new Item(3, "Juice", "ml", 45, ItemType.DRINK)
        );

        List<ItemType> expectedTypes = Collections.singletonList(ItemType.DRINK);

        when(itemService.getItemsByTypes(argThat(
          types -> types.size() == expectedTypes.size() && types.containsAll(expectedTypes))))
          .thenReturn(mockItems);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/items/filter")
            .param("types", "DRINK", "INVALID_TYPE")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});

        assertEquals(2, actualResponses.size());
    }

    /**
     * Test for the sortItems method with GET mapping.
     * This test verifies that the method returns items sorted by name
     * when called with valid sort parameters.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void sortItems_shouldReturnOkWithSortedItems_whenSortByName() throws Exception {
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Apple", "piece", 52, ItemType.FOOD),
          new Item(2, "Bread", "piece", 265, ItemType.FOOD),
          new Item(3, "Water", "liter", 0, ItemType.DRINK)
        );

        when(itemService.getSortedItems("name", "asc")).thenReturn(mockItems);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/items/sort")
            .param("sortBy", "name")
            .param("sortDirection", "asc")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<List<ItemResponse>>() {});

        assertEquals(3, actualResponses.size());
        assertEquals("Apple", actualResponses.get(0).getName());
        assertEquals("Bread", actualResponses.get(1).getName());
        assertEquals("Water", actualResponses.get(2).getName());
    }

    /**
     * Test for the sortItems method with GET mapping using default values.
     * This test verifies that the method defaults to name and asc when
     * no parameters are provided.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void sortItems_shouldUseDefaultValues_whenNoParametersProvided() throws Exception {
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Apple", "piece", 52, ItemType.FOOD),
          new Item(2, "Bread", "piece", 265, ItemType.FOOD)
        );

        when(itemService.getSortedItems("name", "asc")).thenReturn(mockItems);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/items/sort")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<List<ItemResponse>>() {});

        assertEquals(2, actualResponses.size());
    }

    /**
     * Test for the filterAndSortItems method with GET mapping.
     * This test verifies that the method returns filtered and sorted items
     * when called with valid parameters.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void filterAndSortItems_shouldReturnOkWithFilteredAndSortedItems() throws Exception {
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Apple", "piece", 52, ItemType.FOOD),
          new Item(2, "Bread", "piece", 265, ItemType.FOOD)
        );

        List<ItemType> expectedTypes = Collections.singletonList(ItemType.FOOD);

        when(itemService.getFilteredAndSortedItems(
          argThat(types -> types.size() == expectedTypes.size() && types.containsAll(expectedTypes)),
          eq("calories"),
          eq("desc")
        )).thenReturn(mockItems);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/items/filter-and-sort")
            .param("types", "FOOD")
            .param("sortBy", "calories")
            .param("sortDirection", "desc")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<List<ItemResponse>>() {});

        assertEquals(2, actualResponses.size());
        assertEquals("Apple", actualResponses.get(0).getName());
        assertEquals("Bread", actualResponses.get(1).getName());
    }

    /**
     * Test for the filterAndSortItems method with GET mapping using default sort values.
     * This test verifies that the method uses default sort parameters
     * when only filter parameters are provided.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithMockUser
    void filterAndSortItems_shouldUseDefaultSortValues_whenOnlyFilterProvided() throws Exception {
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Apple", "piece", 52, ItemType.FOOD),
          new Item(2, "Bread", "piece", 265, ItemType.FOOD)
        );

        List<ItemType> expectedTypes = Collections.singletonList(ItemType.FOOD);

        when(itemService.getFilteredAndSortedItems(
          argThat(types -> types.size() == expectedTypes.size() && types.containsAll(expectedTypes)),
          eq("name"),
          eq("asc")
        )).thenReturn(mockItems);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/items/filter-and-sort")
            .param("types", "FOOD")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<List<ItemResponse>>() {});

        assertEquals(2, actualResponses.size());
    }
}
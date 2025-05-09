package com.group7.krisefikser.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.item.ItemRequest;
import com.group7.krisefikser.dto.response.item.ItemResponse;
import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.item.Item;
import com.group7.krisefikser.service.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @BeforeEach
    void setup() {
        // Reset all mocks before each test
        reset(itemService);
    }

    @Test
    @WithMockUser
    void getAllItems_shouldReturnOkWithItems_whenServiceReturnsItems() throws Exception {
        // Create mock items
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Water", "liter", 0, ItemType.DRINK),
          new Item(2, "Bread", "piece", 265, ItemType.FOOD)
        );

        // Create expected responses
        List<ItemResponse> mockResponses = mockItems.stream()
          .map(ItemResponse::fromEntity)
          .collect(Collectors.toList());

        // Mock the service methods
        when(itemService.getAllItems()).thenReturn(mockItems);
        when(itemService.convertToItemResponses(mockItems)).thenReturn(mockResponses);

        // Perform the request
        MvcResult result = mockMvc.perform(get("/api/items")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn();

        // Verify the response
        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
        assertEquals(2, actualResponses.size());
        assertEquals(1, actualResponses.get(0).getId());
        assertEquals("Water", actualResponses.get(0).getName());
        assertEquals(ItemType.DRINK, actualResponses.get(0).getType());
    }

    @Test
    @WithMockUser
    void getAllItems_shouldReturnOkWithEmptyList_whenServiceReturnsEmptyList() throws Exception {
        // Mock the service methods
        when(itemService.getAllItems()).thenReturn(Collections.emptyList());
        when(itemService.convertToItemResponses(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Perform the request
        MvcResult result = mockMvc.perform(get("/api/items")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn();

        // Verify the response
        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
        assertTrue(actualResponses.isEmpty());
    }

    @Test
    @WithMockUser
    void getItemById_shouldReturnOkWithItem_whenServiceReturnsItem() throws Exception {
        // Create mock item
        Item mockItem = new Item(1, "Water", "liter", 0, ItemType.DRINK);

        // Mock the service methods
        when(itemService.getItemById(1)).thenReturn(mockItem);

        // Perform the request
        MvcResult result = mockMvc.perform(get("/api/items/1")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn();

        // Verify the response
        String responseContent = result.getResponse().getContentAsString();
        ItemResponse actualResponse = objectMapper.readValue(responseContent, ItemResponse.class);
        assertEquals(1, actualResponse.getId());
        assertEquals("Water", actualResponse.getName());
        assertEquals("liter", actualResponse.getUnit());
        assertEquals(0, actualResponse.getCalories());
        assertEquals(ItemType.DRINK, actualResponse.getType());
    }

    @Test
    @WithMockUser
    void getItemById_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        // Mock the service to throw exception
        when(itemService.getItemById(99)).thenThrow(new RuntimeException("Item not found with id: 99"));

        // Perform the request
        mockMvc.perform(get("/api/items/99")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void addItem_shouldReturnCreatedWithItem_whenValidItemProvided() throws Exception {
        // Create request and response objects
        ItemRequest request = new ItemRequest("Water", "liter", 0, ItemType.DRINK);
        Item savedItem = new Item(1, "Water", "liter", 0, ItemType.DRINK);
        ItemResponse response = ItemResponse.fromEntity(savedItem);

        // Mock the service method
        when(itemService.addItemFromRequest(any(ItemRequest.class))).thenReturn(response);

        // Perform the request
        MvcResult result = mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andReturn();

        // Verify the response
        String responseContent = result.getResponse().getContentAsString();
        ItemResponse actualResponse = objectMapper.readValue(responseContent, ItemResponse.class);
        assertEquals(1, actualResponse.getId());
        assertEquals("Water", actualResponse.getName());
        assertEquals("liter", actualResponse.getUnit());
        assertEquals(0, actualResponse.getCalories());
        assertEquals(ItemType.DRINK, actualResponse.getType());
    }

    @Test
    @WithMockUser
    void addItem_shouldReturnBadRequest_whenInvalidItemProvided() throws Exception {
        // Create invalid request
        ItemRequest request = new ItemRequest("", "", -10, null);

        // Perform the request
        mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateItem_shouldReturnOkWithUpdatedItem_whenValidUpdatePerformed() throws Exception {
        // Create request and response objects
        ItemRequest request = new ItemRequest("Updated Water", "liter", 5, ItemType.DRINK);
        Item updatedItem = new Item(1, "Updated Water", "liter", 5, ItemType.DRINK);
        ItemResponse response = ItemResponse.fromEntity(updatedItem);

        // Mock the service method
        when(itemService.updateItemFromRequest(eq(1), any(ItemRequest.class))).thenReturn(response);

        // Perform the request
        MvcResult result = mockMvc.perform(put("/api/items/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andReturn();

        // Verify the response
        String responseContent = result.getResponse().getContentAsString();
        ItemResponse actualResponse = objectMapper.readValue(responseContent, ItemResponse.class);
        assertEquals(1, actualResponse.getId());
        assertEquals("Updated Water", actualResponse.getName());
        assertEquals("liter", actualResponse.getUnit());
        assertEquals(5, actualResponse.getCalories());
        assertEquals(ItemType.DRINK, actualResponse.getType());
    }

    @Test
    @WithMockUser
    void updateItem_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        // Create request
        ItemRequest request = new ItemRequest("Updated Water", "liter", 5, ItemType.DRINK);

        // Mock the service to throw exception
        when(itemService.updateItemFromRequest(eq(99), any(ItemRequest.class)))
          .thenThrow(new RuntimeException("Item not found with id: 99"));

        // Perform the request
        mockMvc.perform(put("/api/items/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateItem_shouldReturnBadRequest_whenInvalidItemProvided() throws Exception {
        // Create invalid request
        ItemRequest request = new ItemRequest("", "", -10, null);

        // Perform the request
        mockMvc.perform(put("/api/items/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteItem_shouldReturnNoContent_whenSuccessfulDeletion() throws Exception {
        // Mock the service method
        doNothing().when(itemService).deleteItem(1);

        // Perform the request
        mockMvc.perform(delete("/api/items/1")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteItem_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        // Mock the service to throw exception
        doThrow(new RuntimeException("Item not found with id: 99")).when(itemService).deleteItem(99);

        // Perform the request
        mockMvc.perform(delete("/api/items/99")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void filterItems_shouldReturnOkWithFilteredItems_whenValidTypesProvided() throws Exception {
        // Create mock items
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Water", "liter", 0, ItemType.DRINK),
          new Item(3, "Juice", "ml", 45, ItemType.DRINK)
        );

        // Create expected responses
        List<ItemResponse> mockResponses = mockItems.stream()
          .map(ItemResponse::fromEntity)
          .collect(Collectors.toList());

        // Mock the service methods
        when(itemService.convertToItemTypes(anyList())).thenReturn(Collections.singletonList(ItemType.DRINK));
        when(itemService.getItemsByTypes(anyList())).thenReturn(mockItems);
        when(itemService.convertToItemResponses(mockItems)).thenReturn(mockResponses);

        // Perform the request
        MvcResult result = mockMvc.perform(get("/api/items/filter")
            .param("types", "DRINK")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn();

        // Verify the response
        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
        assertEquals(2, actualResponses.size());
        assertEquals(1, actualResponses.get(0).getId());
        assertEquals("Water", actualResponses.get(0).getName());
        assertEquals(ItemType.DRINK, actualResponses.get(0).getType());
    }

    @Test
    @WithMockUser
    void filterItems_shouldFilterOutInvalidTypes_whenInvalidTypesProvided() throws Exception {
        // Create mock items
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Water", "liter", 0, ItemType.DRINK),
          new Item(3, "Juice", "ml", 45, ItemType.DRINK)
        );

        // Create expected responses
        List<ItemResponse> mockResponses = mockItems.stream()
          .map(ItemResponse::fromEntity)
          .collect(Collectors.toList());

        // Mock the service methods - note we still expect only valid types to be passed to getItemsByTypes
        when(itemService.convertToItemTypes(anyList())).thenReturn(Collections.singletonList(ItemType.DRINK));
        when(itemService.getItemsByTypes(anyList())).thenReturn(mockItems);
        when(itemService.convertToItemResponses(mockItems)).thenReturn(mockResponses);

        // Perform the request
        MvcResult result = mockMvc.perform(get("/api/items/filter")
            .param("types", "DRINK", "INVALID_TYPE")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn();

        // Verify the response
        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
        assertEquals(2, actualResponses.size());
    }

    @Test
    @WithMockUser
    void sortItems_shouldReturnOkWithSortedItems_whenSortByName() throws Exception {
        // Create mock items
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Apple", "piece", 52, ItemType.FOOD),
          new Item(2, "Bread", "piece", 265, ItemType.FOOD),
          new Item(3, "Water", "liter", 0, ItemType.DRINK)
        );

        // Create expected responses
        List<ItemResponse> mockResponses = mockItems.stream()
          .map(ItemResponse::fromEntity)
          .collect(Collectors.toList());

        // Mock the service methods
        when(itemService.getSortedItems("name", "asc")).thenReturn(mockItems);
        when(itemService.convertToItemResponses(mockItems)).thenReturn(mockResponses);

        // Perform the request
        MvcResult result = mockMvc.perform(get("/api/items/sort")
            .param("sortBy", "name")
            .param("sortDirection", "asc")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn();

        // Verify the response
        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
        assertEquals(3, actualResponses.size());
        assertEquals("Apple", actualResponses.get(0).getName());
        assertEquals("Bread", actualResponses.get(1).getName());
        assertEquals("Water", actualResponses.get(2).getName());
    }

    @Test
    @WithMockUser
    void sortItems_shouldUseDefaultValues_whenNoParametersProvided() throws Exception {
        // Create mock items
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Apple", "piece", 52, ItemType.FOOD),
          new Item(2, "Bread", "piece", 265, ItemType.FOOD)
        );

        // Create expected responses
        List<ItemResponse> mockResponses = mockItems.stream()
          .map(ItemResponse::fromEntity)
          .collect(Collectors.toList());

        // Mock the service methods - note default values are 'name' and 'asc'
        when(itemService.getSortedItems("name", "asc")).thenReturn(mockItems);
        when(itemService.convertToItemResponses(mockItems)).thenReturn(mockResponses);

        // Perform the request
        MvcResult result = mockMvc.perform(get("/api/items/sort")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn();

        // Verify the response
        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
        assertEquals(2, actualResponses.size());
    }

    @Test
    @WithMockUser
    void filterAndSortItems_shouldReturnOkWithFilteredAndSortedItems() throws Exception {
        // Create mock items
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Apple", "piece", 52, ItemType.FOOD),
          new Item(2, "Bread", "piece", 265, ItemType.FOOD)
        );

        // Create expected responses
        List<ItemResponse> mockResponses = mockItems.stream()
          .map(ItemResponse::fromEntity)
          .collect(Collectors.toList());

        // Mock the service methods
        when(itemService.convertToItemTypes(anyList())).thenReturn(Collections.singletonList(ItemType.FOOD));
        when(itemService.getFilteredAndSortedItems(anyList(), eq("calories"), eq("desc"))).thenReturn(mockItems);
        when(itemService.convertToItemResponses(mockItems)).thenReturn(mockResponses);

        // Perform the request
        MvcResult result = mockMvc.perform(get("/api/items/filter-and-sort")
            .param("types", "FOOD")
            .param("sortBy", "calories")
            .param("sortDirection", "desc")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn();

        // Verify the response
        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
        assertEquals(2, actualResponses.size());
        assertEquals("Apple", actualResponses.get(0).getName());
        assertEquals("Bread", actualResponses.get(1).getName());
    }

    @Test
    @WithMockUser
    void filterAndSortItems_shouldUseDefaultSortValues_whenOnlyFilterProvided() throws Exception {
        // Create mock items
        List<Item> mockItems = Arrays.asList(
          new Item(1, "Apple", "piece", 52, ItemType.FOOD),
          new Item(2, "Bread", "piece", 265, ItemType.FOOD)
        );

        // Create expected responses
        List<ItemResponse> mockResponses = mockItems.stream()
          .map(ItemResponse::fromEntity)
          .collect(Collectors.toList());

        // Mock the service methods - default sort values should be 'name' and 'asc'
        when(itemService.convertToItemTypes(anyList())).thenReturn(Collections.singletonList(ItemType.FOOD));
        when(itemService.getFilteredAndSortedItems(anyList(), eq("name"), eq("asc"))).thenReturn(mockItems);
        when(itemService.convertToItemResponses(mockItems)).thenReturn(mockResponses);

        // Perform the request
        MvcResult result = mockMvc.perform(get("/api/items/filter-and-sort")
            .param("types", "FOOD")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn();

        // Verify the response
        String responseContent = result.getResponse().getContentAsString();
        List<ItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
        assertEquals(2, actualResponses.size());
    }
}
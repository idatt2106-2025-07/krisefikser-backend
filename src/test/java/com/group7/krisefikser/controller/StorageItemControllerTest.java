package com.group7.krisefikser.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.StorageItemRequest;
import com.group7.krisefikser.dto.request.StorageItemSearchRequest;
import com.group7.krisefikser.dto.request.StorageItemSortRequest;
import com.group7.krisefikser.dto.response.AggregatedStorageItemResponse;
import com.group7.krisefikser.dto.response.ItemResponse;
import com.group7.krisefikser.dto.response.StorageItemResponse;
import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.StorageItem;
import com.group7.krisefikser.service.ItemService;
import com.group7.krisefikser.service.StorageItemService;
import com.group7.krisefikser.service.UserService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StorageItemControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private StorageItemService storageItemService;

  @MockitoBean
  private ItemService itemService;

  @MockitoBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  private final int MOCK_HOUSEHOLD_ID = 1;

  @BeforeEach
  void setup() {
    // Reset all mocks before each test
    reset(storageItemService, itemService, userService);

    // Setup the userService to return a mock household ID for all tests
    when(userService.getCurrentUserHouseholdId()).thenReturn(MOCK_HOUSEHOLD_ID);
  }

  @Test
  @WithMockUser
  void getAllStorageItems_shouldReturnOkWithItems_whenServiceReturnsItems() throws Exception {
    // Create mock storage items
    List<StorageItem> mockItems = Arrays.asList(
      createStorageItem(1, 101, MOCK_HOUSEHOLD_ID, 5, LocalDateTime.now().plusDays(10)),
      createStorageItem(2, 102, MOCK_HOUSEHOLD_ID, 3, LocalDateTime.now().plusDays(5))
    );

    // Create mock responses
    List<StorageItemResponse> mockResponses = Arrays.asList(
      createStorageItemResponse(1, 101, MOCK_HOUSEHOLD_ID, 5, LocalDateTime.now().plusDays(10), "Water", true ),
      createStorageItemResponse(2, 102, MOCK_HOUSEHOLD_ID, 3, LocalDateTime.now().plusDays(5), "Bread", false)
    );

    // Mock the service methods
    when(storageItemService.getAllStorageItems(MOCK_HOUSEHOLD_ID)).thenReturn(mockItems);
    when(storageItemService.convertToStorageItemResponses(mockItems)).thenReturn(mockResponses);

    // Perform the request
    MvcResult result = mockMvc.perform(get("/api/storage-items/household")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    // Verify the response
    String responseContent = result.getResponse().getContentAsString();
    List<StorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
    assertEquals(2, actualResponses.size());
    assertEquals(1, actualResponses.get(0).getId());
    assertEquals(101, actualResponses.get(0).getItemId());
    assertEquals(5, actualResponses.get(0).getQuantity());
    assertEquals("Water", actualResponses.get(0).getItem().getName());
  }

  @Test
  @WithMockUser
  void getAllStorageItems_shouldReturnOkWithEmptyList_whenServiceReturnsEmptyList() throws Exception {
    // Mock the service methods
    when(storageItemService.getAllStorageItems(MOCK_HOUSEHOLD_ID)).thenReturn(Collections.emptyList());
    when(storageItemService.convertToStorageItemResponses(Collections.emptyList())).thenReturn(Collections.emptyList());

    // Perform the request
    MvcResult result = mockMvc.perform(get("/api/storage-items/household")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    // Verify the response
    String responseContent = result.getResponse().getContentAsString();
    List<StorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
    assertTrue(actualResponses.isEmpty());
  }

  @Test
  @WithMockUser
  void getExpiringStorageItems_shouldReturnOkWithItems_whenServiceReturnsItems() throws Exception {
    // Create mock storage items
    List<StorageItem> mockItems = Arrays.asList(
      createStorageItem(1, 101, MOCK_HOUSEHOLD_ID, 5, LocalDateTime.now().plusDays(3)),
      createStorageItem(2, 102, MOCK_HOUSEHOLD_ID, 3, LocalDateTime.now().plusDays(5))
    );

    // Create mock responses
    List<StorageItemResponse> mockResponses = Arrays.asList(
      createStorageItemResponse(1, 101, MOCK_HOUSEHOLD_ID, 5, LocalDateTime.now().plusDays(3), "Water", true),
      createStorageItemResponse(2, 102, MOCK_HOUSEHOLD_ID, 3, LocalDateTime.now().plusDays(5), "Bread", false)
    );

    // Mock the service methods
    when(storageItemService.getExpiringStorageItems(7, MOCK_HOUSEHOLD_ID)).thenReturn(mockItems);
    when(storageItemService.convertToStorageItemResponses(mockItems)).thenReturn(mockResponses);

    // Perform the request
    MvcResult result = mockMvc.perform(get("/api/storage-items/household/expiring")
        .param("days", "7")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    // Verify the response
    String responseContent = result.getResponse().getContentAsString();
    List<StorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
    assertEquals(2, actualResponses.size());
  }

  @Test
  @WithMockUser
  void getStorageItemsByItemId_shouldReturnOkWithItems_whenServiceReturnsItems() throws Exception {
    // Create mock storage items
    int itemId = 101;
    List<StorageItem> mockItems = Arrays.asList(
      createStorageItem(1, itemId, MOCK_HOUSEHOLD_ID, 5, LocalDateTime.now().plusDays(10)),
      createStorageItem(2, itemId, MOCK_HOUSEHOLD_ID, 3, LocalDateTime.now().plusDays(5))
    );

    // Create mock responses
    List<StorageItemResponse> mockResponses = Arrays.asList(
      createStorageItemResponse(1, itemId, MOCK_HOUSEHOLD_ID, 5, LocalDateTime.now().plusDays(10), "Water", true),
      createStorageItemResponse(2, itemId, MOCK_HOUSEHOLD_ID, 3, LocalDateTime.now().plusDays(5), "Water", false)
    );

    // Mock the service methods
    when(storageItemService.getStorageItemsByItemId(itemId, MOCK_HOUSEHOLD_ID)).thenReturn(mockItems);
    when(storageItemService.convertToStorageItemResponses(mockItems)).thenReturn(mockResponses);

    // Perform the request
    MvcResult result = mockMvc.perform(get("/api/storage-items/household/by-item/" + itemId)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    // Verify the response
    String responseContent = result.getResponse().getContentAsString();
    List<StorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
    assertEquals(2, actualResponses.size());
    assertEquals(itemId, actualResponses.get(0).getItemId());
    assertEquals(itemId, actualResponses.get(1).getItemId());
  }

  @Test
  @WithMockUser
  void getAggregatedStorageItems_shouldReturnOkWithItems_whenServiceReturnsItems() throws Exception {
    // Create mock aggregated responses
    List<AggregatedStorageItemResponse> mockResponses = Arrays.asList(
      createAggregatedResponse(101, "Water", 8, LocalDateTime.now().plusDays(5), MOCK_HOUSEHOLD_ID, ItemType.DRINK),
      createAggregatedResponse(102, "Bread", 3, LocalDateTime.now().plusDays(10), MOCK_HOUSEHOLD_ID, ItemType.FOOD)
    );

    // Mock the service method
    when(storageItemService.getAggregatedStorageItems(MOCK_HOUSEHOLD_ID)).thenReturn(mockResponses);

    // Perform the request
    MvcResult result = mockMvc.perform(get("/api/storage-items/household/aggregated")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    // Verify the response
    String responseContent = result.getResponse().getContentAsString();
    List<AggregatedStorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
    assertEquals(2, actualResponses.size());
    assertEquals(101, actualResponses.get(0).getItemId());
    assertEquals(8, actualResponses.get(0).getTotalQuantity());
    assertEquals("Water", actualResponses.get(0).getItem().getName());
  }

  @Test
  @WithMockUser
  void sortAggregatedStorageItems_shouldReturnOkWithSortedItems_whenValidSortProvided() throws Exception {
    // Create mock aggregated responses (sorted by quantity descending)
    List<AggregatedStorageItemResponse> mockResponses = Arrays.asList(
      createAggregatedResponse(101, "Water", 8, LocalDateTime.now().plusDays(5), MOCK_HOUSEHOLD_ID, ItemType.DRINK),
      createAggregatedResponse(102, "Bread", 3, LocalDateTime.now().plusDays(10), MOCK_HOUSEHOLD_ID, ItemType.FOOD)
    );

    // Mock the service method
    when(storageItemService.getAggregatedStorageItems(MOCK_HOUSEHOLD_ID, "quantity", "desc")).thenReturn(mockResponses);

    // Create sort request
    StorageItemSortRequest sortRequest = new StorageItemSortRequest();
    sortRequest.setSortBy("quantity");
    sortRequest.setSortDirection("desc");

    // Perform the request
    MvcResult result = mockMvc.perform(get("/api/storage-items/household/aggregated/sort")
        .param("sortBy", "quantity")
        .param("sortDirection", "desc")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    // Verify the response
    String responseContent = result.getResponse().getContentAsString();
    List<AggregatedStorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
    assertEquals(2, actualResponses.size());
    assertEquals(8, actualResponses.get(0).getTotalQuantity());
    assertEquals(3, actualResponses.get(1).getTotalQuantity());
  }

  @Test
  @WithMockUser
  void filterAggregatedStorageItemsByItemType_shouldReturnOkWithFilteredItems_whenValidTypesProvided() throws Exception {
    // Create mock aggregated responses (filtered to only DRINK items)
    List<AggregatedStorageItemResponse> mockResponses = Collections.singletonList(
      createAggregatedResponse(101, "Water", 8, LocalDateTime.now().plusDays(5), MOCK_HOUSEHOLD_ID, ItemType.DRINK)
    );

    // Mock the service methods
    when(itemService.convertToItemTypes(anyList())).thenReturn(Collections.singletonList(ItemType.DRINK));
    when(storageItemService.getFilteredAndSortedAggregatedItems(
      eq(MOCK_HOUSEHOLD_ID),
      eq(Collections.singletonList(ItemType.DRINK)),
      isNull(),
      isNull()
    )).thenReturn(mockResponses);

    // Perform the request
    MvcResult result = mockMvc.perform(get("/api/storage-items/household/aggregated/filter-by-type")
        .param("types", "DRINK")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    // Verify the response
    String responseContent = result.getResponse().getContentAsString();
    List<AggregatedStorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
    assertEquals(1, actualResponses.size());
    assertEquals("Water", actualResponses.get(0).getItem().getName());
    assertEquals(ItemType.DRINK, actualResponses.get(0).getItem().getType());
  }

  @Test
  @WithMockUser
  void filterAndSortAggregatedStorageItems_shouldReturnOkWithFilteredAndSortedItems() throws Exception {
    // Create mock aggregated responses (filtered to FOOD items, sorted by expiration date)
    List<AggregatedStorageItemResponse> mockResponses = Arrays.asList(
      createAggregatedResponse(102, "Bread", 3, LocalDateTime.now().plusDays(5), MOCK_HOUSEHOLD_ID, ItemType.FOOD),
      createAggregatedResponse(103, "Rice", 2, LocalDateTime.now().plusDays(30), MOCK_HOUSEHOLD_ID, ItemType.FOOD)
    );

    // Mock the service methods
    when(itemService.convertToItemTypes(anyList())).thenReturn(Collections.singletonList(ItemType.FOOD));
    when(storageItemService.getFilteredAndSortedAggregatedItems(
      eq(MOCK_HOUSEHOLD_ID),
      eq(Collections.singletonList(ItemType.FOOD)),
      eq("expirationDate"),
      eq("asc")
    )).thenReturn(mockResponses);

    // Perform the request
    MvcResult result = mockMvc.perform(get("/api/storage-items/household/aggregated/filter-and-sort")
        .param("types", "FOOD")
        .param("sortBy", "expirationDate")
        .param("sortDirection", "asc")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    // Verify the response
    String responseContent = result.getResponse().getContentAsString();
    List<AggregatedStorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {});
    assertEquals(2, actualResponses.size());
    assertEquals("Bread", actualResponses.get(0).getItem().getName());
    assertEquals("Rice", actualResponses.get(1).getItem().getName());
  }

  @Test
  @WithMockUser
  void addStorageItem_shouldReturnCreatedWithItem_whenValidItemProvided() throws Exception {
    // Create request object
    StorageItemRequest request = new StorageItemRequest();
    request.setItemId(101);
    request.setQuantity(5);
    request.setExpirationDate(LocalDateTime.now().plusDays(10));

    // Create response object
    StorageItemResponse response = createStorageItemResponse(1, 101, MOCK_HOUSEHOLD_ID, 5,
      LocalDateTime.now().plusDays(10), "Water", true);

    // Mock the service method
    when(storageItemService.addStorageItemFromRequest(eq(MOCK_HOUSEHOLD_ID), any(StorageItemRequest.class)))
      .thenReturn(response);

    // Perform the request
    MvcResult result = mockMvc.perform(post("/api/storage-items")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isCreated())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    // Verify the response
    String responseContent = result.getResponse().getContentAsString();
    StorageItemResponse actualResponse = objectMapper.readValue(responseContent, StorageItemResponse.class);
    assertEquals(1, actualResponse.getId());
    assertEquals(101, actualResponse.getItemId());
    assertEquals(5, actualResponse.getQuantity());
    assertEquals(MOCK_HOUSEHOLD_ID, actualResponse.getHouseholdId());
    assertEquals("Water", actualResponse.getItem().getName());
  }

  @Test
  @WithMockUser
  void updateStorageItem_shouldReturnOkWithUpdatedItem_whenValidUpdatePerformed() throws Exception {
    // Create request object
    int storageItemId = 1;
    StorageItemRequest request = new StorageItemRequest();
    request.setItemId(101);
    request.setQuantity(10); // Updated quantity
    request.setExpirationDate(LocalDateTime.now().plusDays(15)); // Updated date

    // Create response object
    StorageItemResponse response = createStorageItemResponse(storageItemId, 101, MOCK_HOUSEHOLD_ID, 10,
      LocalDateTime.now().plusDays(15), "Water", true);

    // Mock the service method
    when(storageItemService.updateStorageItemFromRequest(eq(storageItemId), eq(MOCK_HOUSEHOLD_ID), any(StorageItemRequest.class)))
      .thenReturn(response);

    // Perform the request
    MvcResult result = mockMvc.perform(put("/api/storage-items/" + storageItemId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    // Verify the response
    String responseContent = result.getResponse().getContentAsString();
    StorageItemResponse actualResponse = objectMapper.readValue(responseContent, StorageItemResponse.class);
    assertEquals(storageItemId, actualResponse.getId());
    assertEquals(101, actualResponse.getItemId());
    assertEquals(10, actualResponse.getQuantity()); // Verify updated quantity
    assertEquals(MOCK_HOUSEHOLD_ID, actualResponse.getHouseholdId());
  }

  @Test
  @WithMockUser
  void updateStorageItem_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
    // Create request object
    int nonExistentItemId = 999;
    StorageItemRequest request = new StorageItemRequest();
    request.setItemId(101);
    request.setQuantity(10);
    request.setExpirationDate(LocalDateTime.now().plusDays(15));

    // Mock the service to throw exception
    when(storageItemService.updateStorageItemFromRequest(eq(nonExistentItemId), eq(MOCK_HOUSEHOLD_ID), any(StorageItemRequest.class)))
      .thenThrow(new RuntimeException("Storage item not found with id: " + nonExistentItemId));

    // Perform the request
    mockMvc.perform(put("/api/storage-items/" + nonExistentItemId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  void deleteStorageItem_shouldReturnNoContent_whenSuccessfulDeletion() throws Exception {
    // Mock the service method
    doNothing().when(storageItemService).deleteStorageItem(1, MOCK_HOUSEHOLD_ID);

    // Perform the request
    mockMvc.perform(delete("/api/storage-items/1")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser
  void deleteStorageItem_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
    // Mock the service to throw exception
    int nonExistentItemId = 999;
    doThrow(new RuntimeException("Storage item not found with id: " + nonExistentItemId))
      .when(storageItemService).deleteStorageItem(nonExistentItemId, MOCK_HOUSEHOLD_ID);

    // Perform the request
    mockMvc.perform(delete("/api/storage-items/" + nonExistentItemId)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }

  // Helper methods to create test data

  private StorageItem createStorageItem(int id, int itemId, int householdId, int quantity, LocalDateTime expirationDate) {
    StorageItem item = new StorageItem();
    item.setId(id);
    item.setItemId(itemId);
    item.setHouseholdId(householdId);
    item.setQuantity(quantity);
    item.setExpirationDate(expirationDate);
    return item;
  }

  private StorageItemResponse createStorageItemResponse(int id, int itemId, int householdId, int quantity,
                                                        LocalDateTime expirationDate, String itemName,
                                                        boolean is_shared) {
    ItemResponse itemResponse = new ItemResponse();
    itemResponse.setId(itemId);
    itemResponse.setName(itemName);
    itemResponse.setUnit("unit");
    itemResponse.setCalories(0);
    itemResponse.setType(itemName.equals("Water") ? ItemType.DRINK : ItemType.FOOD);

    StorageItemResponse response = new StorageItemResponse();
    response.setId(id);
    response.setItemId(itemId);
    response.setHouseholdId(householdId);
    response.setQuantity(quantity);
    response.setExpirationDate(expirationDate);
    response.setShared(is_shared);
    response.setItem(itemResponse);

    return response;
  }

  private AggregatedStorageItemResponse createAggregatedResponse(int itemId, String itemName, int totalQuantity,
                                                                 LocalDateTime earliestExpirationDate,
                                                                 int householdId, ItemType itemType) {
    ItemResponse itemResponse = new ItemResponse();
    itemResponse.setId(itemId);
    itemResponse.setName(itemName);
    itemResponse.setUnit("unit");
    itemResponse.setCalories(0);
    itemResponse.setType(itemType);

    return new AggregatedStorageItemResponse(itemId, itemResponse, totalQuantity, earliestExpirationDate, householdId);
  }
}
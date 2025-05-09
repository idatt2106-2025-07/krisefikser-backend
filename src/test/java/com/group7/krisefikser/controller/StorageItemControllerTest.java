package com.group7.krisefikser.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.item.ChangeStorageItemSharedStatusRequest;
import com.group7.krisefikser.dto.request.item.StorageItemRequest;
import com.group7.krisefikser.dto.request.item.StorageItemSortRequest;
import com.group7.krisefikser.dto.response.item.AggregatedStorageItemResponse;
import com.group7.krisefikser.dto.response.item.ItemResponse;
import com.group7.krisefikser.dto.response.item.StorageItemGroupResponse;
import com.group7.krisefikser.dto.response.item.StorageItemResponse;
import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.item.StorageItem;
import com.group7.krisefikser.service.item.ItemService;
import com.group7.krisefikser.service.item.StorageItemService;
import com.group7.krisefikser.service.user.UserService;
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
import java.util.NoSuchElementException;

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
            createStorageItemResponse(1, 101, MOCK_HOUSEHOLD_ID, 5, LocalDateTime.now().plusDays(10), "Water", true),
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
    List<StorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
    });
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
    List<StorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
    });
    assertTrue(actualResponses.isEmpty());
  }

  @Test
  @WithMockUser
  void getAllSharedStorageItemsInGroup_valid_returnsOk() throws Exception {
    List<AggregatedStorageItemResponse> mockResponses = Arrays.asList(
            createAggregatedResponse(101, "Water", 8, LocalDateTime.now().plusDays(5), ItemType.DRINK),
            createAggregatedResponse(102, "Bread", 3, LocalDateTime.now().plusDays(10), ItemType.FOOD)
    );
    StorageItemSortRequest sortRequest = new StorageItemSortRequest();
    sortRequest.setSortBy("quantity");
    sortRequest.setSortDirection("desc");

    List<String> types = List.of("DRINK", "FOOD");

    when(storageItemService.getSharedStorageItemsInGroup(types, sortRequest))
            .thenReturn(mockResponses);

    MvcResult result = mockMvc.perform(get("/api/storage-items/emergency-group")
                    .param("types", "DRINK")
                    .param("types", "FOOD")
                    .param("sortBy", "quantity")
                    .param("sortDirection", "desc")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    List<AggregatedStorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
    });
    assertEquals(2, actualResponses.size());
    assertEquals(101, actualResponses.get(0).getItemId());
    assertEquals(8, actualResponses.get(0).getTotalQuantity());
    assertEquals("Water", actualResponses.get(0).getItem().getName());
  }

  @Test
  @WithMockUser
  void getAllSharedStorageItemsInGroup_throwsNoSuchElementException_returnsNotFound() throws Exception {
    StorageItemSortRequest sortRequest = new StorageItemSortRequest();
    sortRequest.setSortBy("quantity");
    sortRequest.setSortDirection("desc");

    when(storageItemService.getSharedStorageItemsInGroup(null, sortRequest))
            .thenThrow(new NoSuchElementException("No shared storage items found"));

    mockMvc.perform(get("/api/storage-items/emergency-group")
                    .param("sortBy", "quantity")
                    .param("sortDirection", "desc")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser
  void getAllSharedStorageItemsInGroup_throwsException_returnsInternalServerError() throws Exception {
    StorageItemSortRequest sortRequest = new StorageItemSortRequest();
    sortRequest.setSortBy("quantity");
    sortRequest.setSortDirection("desc");
    when(storageItemService.getSharedStorageItemsInGroup(List.of("FOOD", "DRINK"), sortRequest))
            .thenThrow(new RuntimeException("Some error message"));

    mockMvc.perform(get("/api/storage-items/emergency-group")
                    .param("types", "FOOD")
                    .param("types", "DRINK")
                    .param("sortBy", "quantity")
                    .param("sortDirection", "desc")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser
  void getSharedStorageItemsInGroupByItemId_shouldReturnOkWithItems_whenServiceReturnsItems() throws Exception {
    int itemId = 101;

    List<StorageItemGroupResponse> mockResponses = Arrays.asList(
            new StorageItemGroupResponse(
                    createStorageItemResponse(1, itemId, MOCK_HOUSEHOLD_ID, 5, LocalDateTime.now().plusDays(10), "Water", true),
                    "Household 1"),
            new StorageItemGroupResponse(
                    createStorageItemResponse(2, itemId, 2, 3, LocalDateTime.now().plusDays(5), "Water", false),
                    "Household 2")
    );

    when(storageItemService.getSharedStorageItemsInGroupByItemId(itemId))
            .thenReturn(mockResponses);

    MvcResult result = mockMvc.perform(get("/api/storage-items/emergency-group/by-item/" + itemId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    List<StorageItemGroupResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
    });
    assertEquals(2, actualResponses.size());
    assertEquals(itemId, actualResponses.get(0).getStorageItem().getItemId());
    assertEquals(itemId, actualResponses.get(1).getStorageItem().getItemId());
    assertEquals("Household 1", actualResponses.get(0).getHouseholdName());
    assertEquals("Household 2", actualResponses.get(1).getHouseholdName());
  }

  @Test
  @WithMockUser
  void getAllSharedStorageItemsInGroupById_throwsNoSuchElementException_returnsNotFound() throws Exception {
    int itemId = 101;
    when(storageItemService.getSharedStorageItemsInGroupByItemId(itemId))
            .thenThrow(new NoSuchElementException("Some error message"));

    mockMvc.perform(get("/api/storage-items/emergency-group/by-item/" + itemId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser
  void getAllSharedStorageItemsInGroupById_throwsException_returnsInternalServerError() throws Exception {
    int itemId = 101;
    when(storageItemService.getSharedStorageItemsInGroupByItemId(itemId))
            .thenThrow(new RuntimeException("Some error message"));

    mockMvc.perform(get("/api/storage-items/emergency-group/by-item/" + itemId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
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
    List<StorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
    });
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
    List<StorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
    });
    assertEquals(2, actualResponses.size());
    assertEquals(itemId, actualResponses.get(0).getItemId());
    assertEquals(itemId, actualResponses.get(1).getItemId());
  }

  @Test
  @WithMockUser
  void getAggregatedStorageItems_shouldReturnOkWithItems_whenServiceReturnsItems() throws Exception {
    // Create mock aggregated responses
    List<AggregatedStorageItemResponse> mockResponses = Arrays.asList(
            createAggregatedResponse(101, "Water", 8, LocalDateTime.now().plusDays(5), ItemType.DRINK),
            createAggregatedResponse(102, "Bread", 3, LocalDateTime.now().plusDays(10), ItemType.FOOD)
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
    List<AggregatedStorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
    });
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
            createAggregatedResponse(101, "Water", 8, LocalDateTime.now().plusDays(5), ItemType.DRINK),
            createAggregatedResponse(102, "Bread", 3, LocalDateTime.now().plusDays(10), ItemType.FOOD)
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
    List<AggregatedStorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
    });
    assertEquals(2, actualResponses.size());
    assertEquals(8, actualResponses.get(0).getTotalQuantity());
    assertEquals(3, actualResponses.get(1).getTotalQuantity());
  }

  @Test
  @WithMockUser
  void filterAggregatedStorageItemsByItemType_shouldReturnOkWithFilteredItems_whenValidTypesProvided() throws Exception {
    // Create mock aggregated responses (filtered to only DRINK items)
    List<AggregatedStorageItemResponse> mockResponses = Collections.singletonList(
            createAggregatedResponse(101, "Water", 8, LocalDateTime.now().plusDays(5), ItemType.DRINK)
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
    List<AggregatedStorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
    });
    assertEquals(1, actualResponses.size());
    assertEquals("Water", actualResponses.get(0).getItem().getName());
    assertEquals(ItemType.DRINK, actualResponses.get(0).getItem().getType());
  }

  @Test
  @WithMockUser
  void filterAndSortAggregatedStorageItems_shouldReturnOkWithFilteredAndSortedItems() throws Exception {
    // Create mock aggregated responses (filtered to FOOD items, sorted by expiration date)
    List<AggregatedStorageItemResponse> mockResponses = Arrays.asList(
            createAggregatedResponse(102, "Bread", 3, LocalDateTime.now().plusDays(5), ItemType.FOOD),
            createAggregatedResponse(103, "Rice", 2, LocalDateTime.now().plusDays(30), ItemType.FOOD)
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
    List<AggregatedStorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
    });
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
    request.setQuantity(5.0);
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
    request.setQuantity(10.0); // Updated quantity
    request.setExpirationDate(LocalDateTime.now().plusDays(15)); // Updated date

    // Create response object
    StorageItemResponse response = createStorageItemResponse(storageItemId, 101, MOCK_HOUSEHOLD_ID, 10.0,
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
    request.setQuantity(10.0);
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
  void updateSharedStorageItem_validRequest_returnsOkWithList() throws Exception {
    int storageItemId = 1;
    StorageItemRequest request = new StorageItemRequest();
    request.setItemId(101);
    request.setQuantity(10.0);
    request.setExpirationDate(LocalDateTime.now().plusDays(15));

    StorageItemResponse response = createStorageItemResponse(storageItemId, 101, MOCK_HOUSEHOLD_ID, 10,
            LocalDateTime.now().plusDays(15), "Water", true);

    when(storageItemService.updateSharedStorageItem(storageItemId, request))
            .thenReturn(response);

    mockMvc.perform(put("/api/storage-items/emergency-group/" + storageItemId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(storageItemId))
            .andExpect(jsonPath("$.itemId").value(101))
            .andExpect(jsonPath("$.quantity").value(10));

  }

  @Test
  @WithMockUser
  void updateSharedStorageItem_NoSuchElement_returnsNotFond() throws Exception {
    int storageItemId = 1;
    StorageItemRequest request = new StorageItemRequest();
    request.setItemId(101);
    request.setQuantity(10.0);
    request.setExpirationDate(LocalDateTime.now().plusDays(15));

    when(storageItemService.updateSharedStorageItem(storageItemId, request))
            .thenThrow(new NoSuchElementException("Storage item not found with id: " + storageItemId));

    mockMvc.perform(put("/api/storage-items/emergency-group/" + storageItemId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @WithMockUser
  void updateSharedStorageItem_IllegalArgument_returnsBadRequest() throws Exception {
    int storageItemId = 1;
    StorageItemRequest request = new StorageItemRequest();
    request.setItemId(101);
    request.setQuantity(10.0);
    request.setExpirationDate(LocalDateTime.now().plusDays(15));

    when(storageItemService.updateSharedStorageItem(storageItemId, request))
            .thenThrow(new IllegalArgumentException("Storage item not found with id: " + storageItemId));

    mockMvc.perform(put("/api/storage-items/emergency-group/" + storageItemId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @WithMockUser
  void updateSharedStorageItem_RuntimeException_returnsInternalServerError() throws Exception {
    int storageItemId = 1;
    StorageItemRequest request = new StorageItemRequest();
    request.setItemId(101);
    request.setQuantity(10.0);
    request.setExpirationDate(LocalDateTime.now().plusDays(15));

    when(storageItemService.updateSharedStorageItem(storageItemId, request))
            .thenThrow(new RuntimeException("Storage item not found with id: " + storageItemId));

    mockMvc.perform(put("/api/storage-items/emergency-group/" + storageItemId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").exists());
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

  private StorageItemResponse createStorageItemResponse(int id, int itemId, int householdId, double quantity,
                                                        LocalDateTime expirationDate, String itemName,
                                                        boolean isShared) {
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
    response.setShared(isShared);
    response.setItem(itemResponse);

    return response;
  }

  private AggregatedStorageItemResponse createAggregatedResponse(int itemId, String itemName, int totalQuantity,
                                                                 LocalDateTime earliestExpirationDate,
                                                                 ItemType itemType) {
    ItemResponse itemResponse = new ItemResponse();
    itemResponse.setId(itemId);
    itemResponse.setName(itemName);
    itemResponse.setUnit("unit");
    itemResponse.setCalories(0);
    itemResponse.setType(itemType);

    return new AggregatedStorageItemResponse(itemId, itemResponse, totalQuantity, earliestExpirationDate);
  }

  @Test
  @WithMockUser
  void updateStorageItemSharedStatus_shouldReturnOk_whenValidRequest() throws Exception {
    int storageItemId = 1;
    ChangeStorageItemSharedStatusRequest request = new ChangeStorageItemSharedStatusRequest(
            true,
            10.0
    );

    List<StorageItemResponse> response = List.of(
            createStorageItemResponse(storageItemId, 101, MOCK_HOUSEHOLD_ID, 10, LocalDateTime.now().plusDays(15), "Water", true)
    );

    when(storageItemService.updateStorageItemSharedStatus(storageItemId, MOCK_HOUSEHOLD_ID, request))
            .thenReturn(response);

    mockMvc.perform(patch("/api/storage-items/household/" + storageItemId + "/shared-status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(result -> {
              String responseContent = result.getResponse().getContentAsString();
              List<StorageItemResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
              });
              assertEquals(1, actualResponses.size());
              assertEquals(storageItemId, actualResponses.get(0).getId());
              assertEquals(101, actualResponses.get(0).getItemId());
              assertEquals(10, actualResponses.get(0).getQuantity());
            });
  }

  @Test
  @WithMockUser
  void updateStorageItemSharedStatus_shouldReturnBadRequest_whenIllegalArgument() throws Exception {
    int storageItemId = 1;
    ChangeStorageItemSharedStatusRequest request = new ChangeStorageItemSharedStatusRequest(
            true,
            10.0
    );

    when(storageItemService.updateStorageItemSharedStatus(storageItemId, MOCK_HOUSEHOLD_ID, request))
            .thenThrow(new IllegalArgumentException("Invalid request"));

    mockMvc.perform(patch("/api/storage-items/household/" + storageItemId + "/shared-status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").exists());
  }
  @Test
  @WithMockUser
  void updateStorageItemSharedStatus_shouldReturnNotFound_whenNoSuchElement() throws Exception {
    int storageItemId = 1;
    ChangeStorageItemSharedStatusRequest request = new ChangeStorageItemSharedStatusRequest(
            true,
            10.0
    );

    when(storageItemService.updateStorageItemSharedStatus(storageItemId, MOCK_HOUSEHOLD_ID, request))
            .thenThrow(new NoSuchElementException("Invalid request"));

    mockMvc.perform(patch("/api/storage-items/household/" + storageItemId + "/shared-status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @WithMockUser
  void updateStorageItemSharedStatus_shouldReturnInternalServerError_whenException() throws Exception {
    int storageItemId = 1;
    ChangeStorageItemSharedStatusRequest request = new ChangeStorageItemSharedStatusRequest(
            true,
            10.0
    );

    when(storageItemService.updateStorageItemSharedStatus(storageItemId, MOCK_HOUSEHOLD_ID, request))
            .thenThrow(new RuntimeException("Invalid request"));

    mockMvc.perform(patch("/api/storage-items/household/" + storageItemId + "/shared-status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").exists());
  }


}
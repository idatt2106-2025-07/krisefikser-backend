package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.household.HouseholdJoinRequest;
import com.group7.krisefikser.dto.request.household.HouseholdRequest;
import com.group7.krisefikser.dto.response.household.ReadinessResponse;
import com.group7.krisefikser.model.household.Household;
import com.group7.krisefikser.dto.request.household.JoinHouseholdRequest;
import com.group7.krisefikser.service.household.HouseholdService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HouseholdControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private HouseholdService householdService;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser(username = "1") // This will set the authentication name to "1"
  void createHousehold_shouldReturnCreatedAndHousehold() throws Exception {
    // Create a HouseholdRequest object (what the controller expects)
    HouseholdRequest householdRequest = new HouseholdRequest();
    householdRequest.setName("Test Household");
    householdRequest.setLongitude(10.0);
    householdRequest.setLatitude(60.0);

    Household household = new Household();
    household.setId(1L);
    household.setName("Test Household");
    household.setLongitude(10.0);
    household.setLatitude(60.0);

    when(householdService.createHousehold(
      org.mockito.ArgumentMatchers.any(Household.class),
      org.mockito.ArgumentMatchers.eq(1L)))
      .thenReturn(household);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/households")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(householdRequest)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("Test Household"));
  }

  @Test
  @WithMockUser(username = "3") // Set the authenticated user ID to 3
  void requestToJoin_shouldReturnOkAndJoinRequest() throws Exception {
    // Create the request DTO
    HouseholdJoinRequest joinRequest = new HouseholdJoinRequest();
    joinRequest.setHouseholdId(2L);

    // Create the expected response
    JoinHouseholdRequest responseObj = new JoinHouseholdRequest();
    responseObj.setId(1L);
    responseObj.setHouseholdId(2L);
    responseObj.setUserId(3L);

    // Mock the service call with the expected parameters
    // UserId 3 comes from the security context
    when(householdService.requestToJoin(2L, 3L)).thenReturn(responseObj);

    // Perform the request with JSON body
    mockMvc.perform(MockMvcRequestBuilders.post("/api/households/join-request")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(joinRequest)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.householdId").value(2))
      .andExpect(jsonPath("$.userId").value(3));
  }

  @Test
  @WithMockUser
  void acceptJoinRequest_shouldReturnOk() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/api/households/requests/1/accept"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void declineJoinRequest_shouldReturnOk() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/api/households/requests/1/decline"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "1")
  void shouldReturnReadinessResponse_whenHouseholdExists() throws Exception {
    // Arrange
    ReadinessResponse mockResponse = new ReadinessResponse(3, 12);
    Mockito.when(householdService.calculateReadinessForHousehold()).thenReturn(mockResponse);

    // Act & Assert
    mockMvc.perform(get("/api/households/readiness")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.days").value(3))
        .andExpect(jsonPath("$.hours").value(12));
  }

  @Test
  @WithMockUser
  void shouldReturnNotFound_whenHouseholdDoesNotExist() throws Exception {
    Mockito.when(householdService.calculateReadinessForHousehold()).thenReturn(null);

    mockMvc.perform(get("/api/households/readiness/"))
        .andExpect(status().isNotFound());
  }
}
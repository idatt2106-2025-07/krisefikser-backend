package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.HouseholdJoinRequest;
import com.group7.krisefikser.dto.request.HouseholdRequest;
import com.group7.krisefikser.model.Household;
import com.group7.krisefikser.model.JoinHouseholdRequest;
import com.group7.krisefikser.service.HouseholdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

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
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
      .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Household"));
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
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
      .andExpect(MockMvcResultMatchers.jsonPath("$.householdId").value(2))
      .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(3));
  }

  @Test
  @WithMockUser
  void getRequests_shouldReturnOkAndListOfJoinRequests() throws Exception {
    List<JoinHouseholdRequest> requests = Arrays.asList(
      new JoinHouseholdRequest(1L, 2L, 3L),
      new JoinHouseholdRequest(2L, 2L, 4L)
    );
    when(householdService.getRequestsForHousehold(2L)).thenReturn(requests);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/households/2/requests"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
      .andExpect(MockMvcResultMatchers.jsonPath("$[1].userId").value(4));
  }

  @Test
  @WithMockUser
  void acceptJoinRequest_shouldReturnOk() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/api/households/requests/1/accept"))
      .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser
  void declineJoinRequest_shouldReturnOk() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/api/households/requests/1/decline"))
      .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
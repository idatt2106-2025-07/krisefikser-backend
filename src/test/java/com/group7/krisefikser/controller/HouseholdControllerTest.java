package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.model.Household;
import com.group7.krisefikser.model.JoinHouseholdRequest;
import com.group7.krisefikser.service.HouseholdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
  void createHousehold_shouldReturnOkAndCreatedHousehold() throws Exception {
    Household household = new Household();
    household.setId(1L);
    household.setName("Test Household");
    household.setLongitude(10.0);
    household.setLatitude(60.0);

    when(householdService.createHousehold(org.mockito.ArgumentMatchers.any(Household.class), org.mockito.ArgumentMatchers.anyLong()))
      .thenReturn(household);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/households/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(household))
        .param("userId", "1"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
      .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Household"));
  }

  @Test
  void requestToJoin_shouldReturnOkAndJoinRequest() throws Exception {
    JoinHouseholdRequest request = new JoinHouseholdRequest();
    request.setId(1L);
    request.setHouseholdId(2L);
    request.setUserId(3L);

    when(householdService.requestToJoin(2L, 3L)).thenReturn(request);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/households/join-request")
        .param("householdId", "2")
        .param("userId", "3"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
      .andExpect(MockMvcResultMatchers.jsonPath("$.householdId").value(2))
      .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(3));
  }

  @Test
  void getRequests_shouldReturnOkAndListOfRequests() throws Exception {
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
  void acceptRequest_shouldReturnOk() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/api/households/requests/1/accept"))
      .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void declineRequest_shouldReturnOk() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/api/households/requests/1/decline"))
      .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
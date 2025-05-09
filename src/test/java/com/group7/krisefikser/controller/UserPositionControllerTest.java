package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.location.SharePositionRequest;
import com.group7.krisefikser.dto.response.location.GroupMemberPositionResponse;
import com.group7.krisefikser.dto.response.location.HouseholdMemberPositionResponse;
import com.group7.krisefikser.service.location.UserPositionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserPositionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserPositionService userPositionService;

  @Autowired
  private ObjectMapper objectMapper;

  private SharePositionRequest validRequest;

  @BeforeEach
  void setUp() {
    validRequest = new SharePositionRequest();
    validRequest.setLatitude(59.9139);
    validRequest.setLongitude(10.7522);
  }

  @Test
  @WithMockUser
  void testSharePosition_Success() throws Exception {
    doNothing().when(userPositionService).sharePosition(Mockito.any());

    mockMvc.perform(post("/api/position/share")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
        .andExpect(status().isOk())
        .andExpect(content().string("Position shared successfully"));
  }

  @Test
  @WithMockUser
  void testStopSharingPosition_Success() throws Exception {
    doNothing().when(userPositionService).deleteUserPosition();

    mockMvc.perform(delete("/api/position/delete"))
        .andExpect(status().isOk())
        .andExpect(content().string("Stopped sharing position successfully"));
  }

  @Test
  @WithMockUser
  void testGetHouseholdPosition_Success() throws Exception {
    HouseholdMemberPositionResponse response = new HouseholdMemberPositionResponse();
    response.setName("TestUser");
    response.setLatitude(60.0);
    response.setLongitude(11.0);

    HouseholdMemberPositionResponse[] responses = new HouseholdMemberPositionResponse[]{response};

    when(userPositionService.getHouseholdPositions()).thenReturn(responses);

    mockMvc.perform(get("/api/position/household"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("TestUser"))
        .andExpect(jsonPath("$[0].latitude").value(60.0))
        .andExpect(jsonPath("$[0].longitude").value(11.0));
  }

  @Test
  @WithMockUser
  void testGetGroupPosition_Success() throws Exception {
    // Create sample response
    GroupMemberPositionResponse response1 = new GroupMemberPositionResponse();
    response1.setName("GroupMember1");
    response1.setLatitude(59.5);
    response1.setLongitude(10.2);

    GroupMemberPositionResponse response2 = new GroupMemberPositionResponse();
    response2.setName("GroupMember2");
    response2.setLatitude(59.6);
    response2.setLongitude(10.3);

    GroupMemberPositionResponse[] responses =
        new GroupMemberPositionResponse[]{response1, response2};

    // Mock the service method
    when(userPositionService.getGroupPositions()).thenReturn(responses);

    // Perform the request and verify response
    mockMvc.perform(get("/api/position/group"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("GroupMember1"))
        .andExpect(jsonPath("$[0].latitude").value(59.5))
        .andExpect(jsonPath("$[0].longitude").value(10.2))
        .andExpect(jsonPath("$[1].name").value("GroupMember2"))
        .andExpect(jsonPath("$[1].latitude").value(59.6))
        .andExpect(jsonPath("$[1].longitude").value(10.3));
  }

  @Test
  @WithMockUser
  void testGetGroupPosition_Error() throws Exception {
    // Mock service to throw an exception
    when(userPositionService.getGroupPositions())
        .thenThrow(new RuntimeException("Test error"));

    // Perform the request and verify error response
    mockMvc.perform(get("/api/position/group"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error retrieving group positions"));
  }
}

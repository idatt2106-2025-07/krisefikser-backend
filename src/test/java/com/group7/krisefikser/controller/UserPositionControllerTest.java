package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.SharePositionRequest;
import com.group7.krisefikser.dto.response.HouseholdMemberPositionResponse;
import com.group7.krisefikser.service.UserPositionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.UpperCase;
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
}

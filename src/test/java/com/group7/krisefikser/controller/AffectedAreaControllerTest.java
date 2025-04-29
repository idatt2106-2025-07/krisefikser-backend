package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.response.AffectedAreaResponse;
import com.group7.krisefikser.service.AffectedAreaService;
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
class AffectedAreaControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private AffectedAreaService affectedAreaService;
  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Test for the getAllAffectedAreas method in the AffectedAreaController.
   * This test verifies that the method returns a list of affected areas
   * in JSON format with a 200 OK status.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  void getAllAffectedAreas_shouldReturnOkAndJsonListOfAreas() throws Exception {
    List<AffectedAreaResponse> mockResponses = Arrays.asList(
            new AffectedAreaResponse(1L, 10.0, 60.0, 5.0, 1, "High danger area 1", null),
            new AffectedAreaResponse(2L, 11.0, 61.0, 3.0, 2, "Medium danger area 2", null)
    );
    when(affectedAreaService.getAllAffectedAreas()).thenReturn(mockResponses);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/affected-area"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].longitude").value(10.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].latitude").value(61.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].dangerRadiusKm").value(5.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].severityLevel").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Medium danger area 2"));
  }

  /**
   * Test for the getAllAffectedAreas method in the AffectedAreaController.
   * This test verifies that the method returns an empty list of affected areas
   * in JSON format with a 200 OK status when no areas are found.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  void getAllAffectedAreas_shouldReturnInternalServerError_onServiceException() throws Exception {
    when(affectedAreaService.getAllAffectedAreas()).thenThrow(new RuntimeException("Simulated service error"));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/affected-area"))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0)); // Expect an empty list as per your controller
  }
}

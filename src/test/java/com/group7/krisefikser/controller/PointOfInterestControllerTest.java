package com.group7.krisefikser.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.GetPointsOfInterestRequest;
import com.group7.krisefikser.dto.response.PointOfInterestResponse;
import com.group7.krisefikser.service.PointOfInterestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
import static org.mockito.Mockito.when;

/**
 * Test class for the PointOfInterestController.
 * This class contains unit tests for the PointOfInterestController methods.
 * It uses MockMvc to perform requests and verify responses.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PointOfInterestControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PointOfInterestService pointOfInterestService;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Test for the getPointsOfInterest method.
   * This test verifies that the method returns a list of points of interest
   * when called with a valid request.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  void getPointsOfInterest_shouldReturnOkWithPoints_whenServiceReturnsPoints() throws Exception {
    GetPointsOfInterestRequest request = new GetPointsOfInterestRequest(Arrays.asList("SHELTER"));
    List<PointOfInterestResponse> mockResponses = Collections.singletonList(
            new PointOfInterestResponse(1L, 63.4297, 10.3933, "SHELTER")
    );
    when(pointOfInterestService.getPointsOfInterestByTypes(request)).thenReturn(mockResponses);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/point-of-interest")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    List<PointOfInterestResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<List<PointOfInterestResponse>>() {
    });
    assertEquals(1, actualResponses.size());
    assertEquals(1L, actualResponses.get(0).getId());
    assertEquals("SHELTER", actualResponses.get(0).getType());
  }

  /**
   * Test for the getPointsOfInterest method.
   * This test verifies that the method returns an empty list
   * when the service returns an empty list.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  void getPointsOfInterest_shouldReturnOkWithEmptyList_whenServiceReturnsEmptyList() throws Exception {
    GetPointsOfInterestRequest request = new GetPointsOfInterestRequest(Collections.singletonList("SUPPLY_DEPOT"));
    when(pointOfInterestService.getPointsOfInterestByTypes(request)).thenReturn(Collections.emptyList());

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/point-of-interest")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    List<PointOfInterestResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<List<PointOfInterestResponse>>() {
    });
    assertTrue(actualResponses.isEmpty());
  }

  /**
   * Test for the getPointsOfInterest method.
   * This test verifies that the method returns a bad request status
   * when an IllegalArgumentException is thrown by the service.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  void getPointsOfInterest_shouldReturnBadRequestWithEmptyList_whenIllegalArgumentException() throws Exception {
    GetPointsOfInterestRequest request = new GetPointsOfInterestRequest(Collections.singletonList("INVALID_TYPE"));
    when(pointOfInterestService.getPointsOfInterestByTypes(request))
            .thenThrow(new IllegalArgumentException("Invalid point of interest type provided"));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/point-of-interest")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    List<PointOfInterestResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<List<PointOfInterestResponse>>() {
    });
    assertTrue(actualResponses.isEmpty());
  }

  /**
   * Test for the getPointsOfInterest method.
   * This test verifies that the method returns an internal server error status
   * when an unexpected exception is thrown by the service.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  void getPointsOfInterest_shouldReturnInternalServerErrorWithEmptyList_whenUnexpectedException() throws Exception {
    GetPointsOfInterestRequest request = new GetPointsOfInterestRequest(Collections.singletonList("SHELTER"));
    when(pointOfInterestService.getPointsOfInterestByTypes(request)).thenThrow(new RuntimeException("Database error"));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/point-of-interest")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    List<PointOfInterestResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<List<PointOfInterestResponse>>() {
    });
    assertTrue(actualResponses.isEmpty());
  }
}

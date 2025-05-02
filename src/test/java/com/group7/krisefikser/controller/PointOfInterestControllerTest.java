package com.group7.krisefikser.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.GetPointsOfInterestRequest;
import com.group7.krisefikser.dto.request.PointOfInterestRequest;
import com.group7.krisefikser.dto.response.PointOfInterestResponse;
import com.group7.krisefikser.service.PointOfInterestService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

  private static final String BASE_URL = "/api/point-of-interest";
  private static final String AUTHORIZATION_HEADER = "Bearer validToken";
  private static final long TEST_ID = 1L;

  private PointOfInterestRequest createValidPointOfInterestRequest() {
    PointOfInterestRequest request = new PointOfInterestRequest();
    request.setLatitude(10.0);
    request.setLongitude(20.0);
    request.setType("shelter");
    request.setOpensAt("08:00");
    request.setClosesAt("20:00");
    request.setContactNumber("12345678");
    request.setDescription("A test point of interest");
    return request;
  }

  private PointOfInterestResponse createValidPointOfInterestResponse(Long id) {
    PointOfInterestResponse response = new PointOfInterestResponse();
    response.setId(id);
    response.setLatitude(10.0);
    response.setLongitude(20.0);
    response.setType("shelter");
    response.setOpensAt("08:00");
    response.setClosesAt("20:00");
    response.setContactNumber("12345678");
    response.setDescription("A test point of interest");
    return response;
  }

  /**
   * Test for the getPointsOfInterest method.
   * This test verifies that the method returns a list of points of interest
   * when called with a valid request.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  void getPointsOfInterest_shouldReturnOkWithPoints_whenServiceReturnsPoints() throws Exception {
    GetPointsOfInterestRequest request = new GetPointsOfInterestRequest(Arrays.asList("SHELTER", "WATER_STATION"));
    String requestString = "?types=SHELTER&types=WATER_STATION";
    List<PointOfInterestResponse> mockResponses = Arrays.asList(
            new PointOfInterestResponse(1L, 63.4297, 10.3933, "SHELTER"
                    , "08:00", "20:00", "123456789", "A shelter for people in need"),
            new PointOfInterestResponse(2L, 63.4297, 10.3933, "WATER_STATION",
                    "08:00", "20:00", "123456789", "A water station for people in need")
    );
    when(pointOfInterestService.getPointsOfInterestByTypes(request)).thenReturn(mockResponses);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/point-of-interest" + requestString))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    List<PointOfInterestResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<List<PointOfInterestResponse>>() {
    });
    assertEquals(2, actualResponses.size());
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
  void getPointsOfInterest_shouldReturnBadRequestWithInvalidType_whenIllegalArgumentException() throws Exception {
    GetPointsOfInterestRequest request = new GetPointsOfInterestRequest(Collections.singletonList("INVALID_TYPE"));
    String requestString = "?types=INVALID_TYPE";
    when(pointOfInterestService.getPointsOfInterestByTypes(request))
            .thenThrow(new IllegalArgumentException("Invalid point of interest type provided"));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/point-of-interest" + requestString))
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
    String requestString = "?types=SHELTER";
    when(pointOfInterestService.getPointsOfInterestByTypes(request)).thenThrow(new RuntimeException("Database error"));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/point-of-interest" + requestString))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    List<PointOfInterestResponse> actualResponses = objectMapper.readValue(responseContent, new TypeReference<List<PointOfInterestResponse>>() {
    });
    assertTrue(actualResponses.isEmpty());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void addPointOfInterest_validRequest_returnsCreated() throws Exception {
    PointOfInterestRequest request = createValidPointOfInterestRequest();
    PointOfInterestResponse response = createValidPointOfInterestResponse(TEST_ID);

    when(pointOfInterestService.addPointOfInterest(any(PointOfInterestRequest.class)))
            .thenReturn(response);

    mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                    .header("Authorization", AUTHORIZATION_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.header().string("Location",
                    "http://localhost" + BASE_URL + "/" + TEST_ID))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType
                    .APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(TEST_ID))
            .andExpect(MockMvcResultMatchers.jsonPath("$.latitude").value(request.getLatitude()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(request.getLongitude()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(request.getDescription()));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void addPointOfInterest_invalidRequest_returnsBadRequest() throws Exception {
    PointOfInterestRequest invalidRequest = new PointOfInterestRequest();

    when(pointOfInterestService.addPointOfInterest(any(PointOfInterestRequest.class)))
            .thenThrow(new IllegalArgumentException("Request is missing required fields."));

    mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                    .header("Authorization", AUTHORIZATION_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @WithMockUser(roles = "NORMAL")
  void addPointOfInterest_unauthorized_returnsForbidden() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void addPointOfInterest_internalServerError_returnsInternalServerError() throws Exception {
    PointOfInterestRequest request = createValidPointOfInterestRequest();
    String errorMessage = "Internal server error while adding point of interest: Something went wrong.";

    when(pointOfInterestService.addPointOfInterest(any(PointOfInterestRequest.class)))
            .thenThrow(new RuntimeException("Something went wrong."));

    mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                    .header("Authorization", AUTHORIZATION_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void deletePointOfInterest_validId_returnsNoContent() throws Exception {
    doNothing().when(pointOfInterestService).deletePointOfInterest(TEST_ID);

    mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + TEST_ID)
                    .header("Authorization", AUTHORIZATION_HEADER))
            .andExpect(MockMvcResultMatchers.status().isNoContent());

    verify(pointOfInterestService, times(1)).deletePointOfInterest(TEST_ID);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void deletePointOfInterest_invalidId_returnsBadRequest() throws Exception {
    String errorMessage = "Invalid point of interest ID provided: ID must be a positive number.";

    doThrow(new IllegalArgumentException("ID must be a positive number."))
            .when(pointOfInterestService).deletePointOfInterest(TEST_ID);

    mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + TEST_ID)
                    .header("Authorization", AUTHORIZATION_HEADER))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
  }

  @Test
  @WithMockUser(roles = "NORMAL")
  void deletePointOfInterest_unauthorized_returnsForbidden() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + TEST_ID))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void deletePointOfInterest_internalServerError_returnsInternalServerError() throws Exception {
    String errorMessage = "Internal server error while deleting point of interest: Database error.";

    doThrow(new RuntimeException("Database error."))
            .when(pointOfInterestService).deletePointOfInterest(TEST_ID);

    mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + TEST_ID)
                    .header("Authorization", AUTHORIZATION_HEADER))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void updatePointOfInterest_validRequest_returnsOk() throws Exception {
    PointOfInterestRequest request = createValidPointOfInterestRequest();
    PointOfInterestResponse response = createValidPointOfInterestResponse(TEST_ID);

    when(pointOfInterestService.updatePointOfInterest(eq(TEST_ID), any(PointOfInterestRequest.class)))
            .thenReturn(response);

    mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + TEST_ID)
                    .header("Authorization", AUTHORIZATION_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(TEST_ID))
            .andExpect(MockMvcResultMatchers.jsonPath("$.latitude").value(request.getLatitude()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(request.getLongitude()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(request.getDescription()));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void updatePointOfInterest_invalidRequest_returnsBadRequest() throws Exception {
    PointOfInterestRequest invalidRequest = new PointOfInterestRequest();

    when(pointOfInterestService.updatePointOfInterest(eq(TEST_ID), any(PointOfInterestRequest.class)))
            .thenThrow(new IllegalArgumentException("Missing name."));

    mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + TEST_ID)
                    .header("Authorization", AUTHORIZATION_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @WithMockUser(roles = "NORMAL")
  void updatePointOfInterest_unauthorized_returnsForbidden() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + TEST_ID))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void updatePointOfInterest_internalServerError_returnsInternalServerError() throws Exception {
    PointOfInterestRequest request = createValidPointOfInterestRequest();
    String errorMessage = "Internal server error while updating point of interest: Database connection failed.";

    when(pointOfInterestService.updatePointOfInterest(eq(TEST_ID), any(PointOfInterestRequest.class)))
            .thenThrow(new RuntimeException("Database connection failed."));

    mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + TEST_ID)
                    .header("Authorization", AUTHORIZATION_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
  }
}

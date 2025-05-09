package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.location.AffectedAreaRequest;
import com.group7.krisefikser.dto.response.location.AffectedAreaResponse;
import com.group7.krisefikser.service.location.AffectedAreaService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

  private static final String BASE_URL = "/api/affected-area";
  private AffectedAreaRequest testAreaRequest1;
  private AffectedAreaResponse testAreaResponse1;
  private final long testId = 1L;

  @BeforeEach
  void setUp() {
    testAreaRequest1 = new AffectedAreaRequest();
    testAreaRequest1.setName("Name");
    testAreaRequest1.setLongitude(11.5);
    testAreaRequest1.setLatitude(63.5);
    testAreaRequest1.setHighDangerRadiusKm(6.0);
    testAreaRequest1.setMediumDangerRadiusKm(11.0);
    testAreaRequest1.setLowDangerRadiusKm(16.0);
    testAreaRequest1.setSeverityLevel(2);
    testAreaRequest1.setDescription("Test Area 1");
    testAreaRequest1.setStartDate(LocalDateTime.now().minusDays(2)
            .format(DateTimeFormatter.ISO_DATE_TIME));

    testAreaResponse1 = new AffectedAreaResponse();
    testAreaResponse1.setId(testId);
    testAreaResponse1.setName(testAreaRequest1.getName());
    testAreaResponse1.setLongitude(testAreaRequest1.getLongitude());
    testAreaResponse1.setLatitude(testAreaRequest1.getLatitude());
    testAreaResponse1.setHighDangerRadiusKm(testAreaRequest1.getHighDangerRadiusKm());
    testAreaResponse1.setMediumDangerRadiusKm(testAreaRequest1.getMediumDangerRadiusKm());
    testAreaResponse1.setLowDangerRadiusKm(testAreaRequest1.getLowDangerRadiusKm());
    testAreaResponse1.setSeverityLevel(testAreaRequest1.getSeverityLevel());
    testAreaResponse1.setDescription(testAreaRequest1.getDescription());
    testAreaResponse1.setStartDate(testAreaRequest1.getStartDate());
  }

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
            new AffectedAreaResponse(1L, "Name 1", 10.0, 60.0, 5.0, 6.1, 7.0, 1, "High danger area 1", null),
            new AffectedAreaResponse(2L, "Name 2", 11.0, 61.0, 3.0, 4.1, 4.9, 2, "Medium danger area 2", null)
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
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].highDangerRadiusKm").value(5.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].mediumDangerRadiusKm").value(6.1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].lowDangerRadiusKm").value(7.0))
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

  @Test
  @WithMockUser(roles = "ADMIN")
  void addAffectedArea_validInput_returnsCreated() throws Exception {
    when(affectedAreaService.addAffectedArea(any(AffectedAreaRequest.class))).thenReturn(testAreaResponse1);

    mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testAreaRequest1)))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(testAreaResponse1.getLongitude()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.latitude").value(testAreaResponse1.getLatitude()))
            .andExpect(MockMvcResultMatchers.header().string("Location", ServletUriComponentsBuilder.fromCurrentRequestUri().path(BASE_URL + "/{id}").buildAndExpand(testId).toUriString()));
  }

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void addAffectedArea_invalidInput_returnsBadRequest() throws Exception {
    AffectedAreaRequest invalidRequest = new AffectedAreaRequest();
    mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void addAffectedArea_serviceThrowsIllegalStateException_returnsBadRequestWithErrorResponse() throws Exception {
    String errorMessage = "Failed to add affected area due to validation";
    when(affectedAreaService.addAffectedArea(any(AffectedAreaRequest.class))).thenThrow(new IllegalStateException(errorMessage));

    mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testAreaRequest1)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void addAffectedArea_serviceThrowsException_returnsInternalServerError() throws Exception {
    String errorMessage = "Database error";
    when(affectedAreaService.addAffectedArea(any(AffectedAreaRequest.class))).thenThrow(new RuntimeException(errorMessage));

    mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testAreaRequest1)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.content().string("Error adding affected area"));
  }
  @Test
  @WithMockUser(roles = "NORMAL")
  void addAffectedArea_normalUserRole_returnsForbidden() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testAreaRequest1)))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void deleteAffectedArea_validId_returnsNoContent() throws Exception {
    doNothing().when(affectedAreaService).deleteAffectedArea(testId);

    mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + testId))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void deleteAffectedArea_invalidId_returnsNotFoundWithErrorResponse() throws Exception {
    String errorMessage = "Affected area with ID " + testId + " not found";
    doThrow(new IllegalArgumentException(errorMessage)).when(affectedAreaService)
            .deleteAffectedArea(testId);

    mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + testId))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void deleteAffectedArea_serviceThrowsException_returnsInternalServerError() throws Exception {
    String errorMessage = "Database error during deletion";
    doThrow(new RuntimeException(errorMessage)).when(affectedAreaService).deleteAffectedArea(testId);

    mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + testId))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.content().string("A server error occurred while deleting affected area"));
  }

  @Test
  @WithMockUser
  void deleteAffectedArea_normalUserRole_returnsForbidden() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + testId))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void updateAffectedArea_validIdAndInput_returnsOkWithUpdatedArea() throws Exception {
    when(affectedAreaService.updateAffectedArea(eq(testId), any(AffectedAreaRequest.class))).thenReturn(testAreaResponse1);

    mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + testId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testAreaRequest1)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(testAreaResponse1.getLongitude()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.latitude").value(testAreaResponse1.getLatitude()));
  }

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void updateAffectedArea_invalidInput_returnsBadRequest() throws Exception {
    AffectedAreaRequest invalidRequest = new AffectedAreaRequest();

    mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + testId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void updateAffectedArea_serviceThrowsIllegalStateException_returnsBadRequestWithErrorResponse() throws Exception {
    String errorMessage = "Failed to update affected area due to invalid data";
    when(affectedAreaService.updateAffectedArea(eq(testId), any(AffectedAreaRequest.class))).thenThrow(new IllegalStateException(errorMessage));

    mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + testId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testAreaRequest1)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void updateAffectedArea_serviceThrowsException_returnsInternalServerError() throws Exception {
    String errorMessage = "Database error during update";
    when(affectedAreaService.updateAffectedArea(eq(testId), any(AffectedAreaRequest.class))).thenThrow(new RuntimeException(errorMessage));

    mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + testId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testAreaRequest1)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.content().string("Error updating affected area"));
  }

  @Test
  @WithMockUser
  void updateAffectedArea_normalUserRole_returnsForbidden() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + testId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testAreaRequest1)))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
  }
}

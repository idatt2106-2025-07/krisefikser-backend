package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.EmergencyGroupRequest;
import com.group7.krisefikser.dto.request.InvitationReplyRequest;
import com.group7.krisefikser.dto.response.EmergencyGroupResponse;
import com.group7.krisefikser.dto.response.ErrorResponse;
import com.group7.krisefikser.service.EmergencyGroupService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EmergencyGroupControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;
  private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  @MockitoBean
  private EmergencyGroupService emergencyGroupService;

  @Test
  @WithMockUser
  void getEmergencyGroupById_existingId_returnsOkAndEmergencyGroupResponseWithDate() throws Exception {
    Long groupId = 1L;
    Date creationDate = new Date();
    EmergencyGroupResponse mockResponse = new EmergencyGroupResponse(groupId, "Group Alpha", creationDate.toString());
    when(emergencyGroupService.getEmergencyGroupById(groupId)).thenReturn(mockResponse);

    mockMvc.perform(get("/api/emergency-groups/{id}", groupId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
  }

  @Test
  @WithMockUser
  void getEmergencyGroupById_nonExistingId_returnsNotFoundAndErrorResponseWithDate() throws Exception {
    // Arrange
    Long groupId = 99L;
    when(emergencyGroupService.getEmergencyGroupById(groupId)).thenThrow(NoSuchElementException.class);
    ErrorResponse expectedResponse = new ErrorResponse("Emergency group not found. The emergency group with the specified ID does not exist.");

    // Act & Assert
    mockMvc.perform(get("/api/emergency-groups/{id}", groupId))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @WithMockUser
  void getEmergencyGroupById_serviceThrowsException_returnsInternalServerError() throws Exception {
    // Arrange
    Long groupId = 1L;
    when(emergencyGroupService.getEmergencyGroupById(groupId)).thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    mockMvc.perform(get("/api/emergency-groups/{id}", groupId))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("An error occurred while retrieving the emergency group."));
  }

  @Test
  @WithMockUser
  void addEmergencyGroup_validRequest_returnsCreatedAndEmergencyGroupResponseWithDate() throws Exception {
    // Arrange
    EmergencyGroupRequest request = new EmergencyGroupRequest();
    request.setName("Group Beta");
    request.setCreatedAt(LocalDateTime.now().format(formatter));
    Date creationDate = new Date();
    EmergencyGroupResponse mockResponse = new EmergencyGroupResponse(2L, "Group Beta", creationDate.toString());
    when(emergencyGroupService.addEmergencyGroup(any(EmergencyGroupRequest.class))).thenReturn(mockResponse);

    // Act & Assert
    mockMvc.perform(post("/api/emergency-groups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
  }

  @Test
  @WithMockUser
  void addEmergencyGroup_invalidRequest_returnsBadRequestAndErrorResponseWithDate() throws Exception {
    // Arrange
    EmergencyGroupRequest request = new EmergencyGroupRequest();
    request.setName("");
    when(emergencyGroupService.addEmergencyGroup(any(EmergencyGroupRequest.class))).thenThrow(new IllegalArgumentException("Emergency group name cannot be empty."));
    ErrorResponse expectedResponse = new ErrorResponse("Emergency group name cannot be empty.");

    // Act & Assert
    mockMvc.perform(post("/api/emergency-groups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @WithMockUser
  void addEmergencyGroup_serviceThrowsException_returnsInternalServerError() throws Exception {
    // Arrange
    EmergencyGroupRequest request = new EmergencyGroupRequest();
    request.setName("Group Gamma");
    when(emergencyGroupService.addEmergencyGroup(any(EmergencyGroupRequest.class))).thenThrow(new RuntimeException("Failed to save"));

    // Act & Assert
    mockMvc.perform(post("/api/emergency-groups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("An error occurred while adding the emergency group."));
  }

  @Test
  @WithMockUser
  void inviteHouseholdByName_Success() throws Exception {
    String householdName = "Test Household";
    Mockito.doNothing().when(emergencyGroupService).inviteHouseholdByName(householdName);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/emergency-groups/invite/{householdName}", householdName))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Household invited successfully."));

    Mockito.verify(emergencyGroupService, Mockito.times(1)).inviteHouseholdByName(householdName);
  }

  @Test
  @WithMockUser
  void inviteHouseholdByName_NotFound() throws Exception {
    String householdName = "NonExistent Household";
    Mockito.doThrow(new NoSuchElementException("Household not found")).when(emergencyGroupService).inviteHouseholdByName(householdName);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/emergency-groups/invite/{householdName}", householdName))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Household not found."));

    Mockito.verify(emergencyGroupService, Mockito.times(1)).inviteHouseholdByName(householdName);
  }

  @Test
  @WithMockUser
  void inviteHouseholdByName_IllegalArgument() throws Exception {
    String householdName = "Invalid Household";
    String errorMessage = "Invalid household name format.";
    Mockito.doThrow(new IllegalArgumentException(errorMessage)).when(emergencyGroupService).inviteHouseholdByName(householdName);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/emergency-groups/invite/{householdName}", householdName))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));

    Mockito.verify(emergencyGroupService, Mockito.times(1)).inviteHouseholdByName(householdName);
  }

  @Test
  @WithMockUser
  void inviteHouseholdByName_UnexpectedError() throws Exception {
    String householdName = "Problematic Household";
    String errorMessage = "Database connection error.";
    Mockito.doThrow(new RuntimeException(errorMessage)).when(emergencyGroupService).inviteHouseholdByName(householdName);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/emergency-groups/invite/{householdName}", householdName))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("An unexpected error occurred while inviting the household."));

    Mockito.verify(emergencyGroupService, Mockito.times(1)).inviteHouseholdByName(householdName);
  }

  @Test
  @WithMockUser
  void answerInvitation_Success_Accept() throws Exception {
    Long groupId = 123L;
    InvitationReplyRequest request = new InvitationReplyRequest(true);
    Mockito.doNothing().when(emergencyGroupService).answerEmergencyGroupInvitation(groupId, true);

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/emergency-groups/answer-invitation/{groupId}", groupId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Invitation answered successfully."));

    Mockito.verify(emergencyGroupService, Mockito.times(1)).answerEmergencyGroupInvitation(groupId, true);
  }

  @Test
  @WithMockUser
  void answerInvitation_Success_Decline() throws Exception {
    Long groupId = 456L;
    InvitationReplyRequest request = new InvitationReplyRequest(false);
    Mockito.doNothing().when(emergencyGroupService).answerEmergencyGroupInvitation(groupId, false);

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/emergency-groups/answer-invitation/{groupId}", groupId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Invitation answered successfully."));

    Mockito.verify(emergencyGroupService, Mockito.times(1)).answerEmergencyGroupInvitation(groupId, false);
  }

  @Test
  @WithMockUser
  void answerInvitation_NotFound() throws Exception {
    Long groupId = 789L;
    InvitationReplyRequest request = new InvitationReplyRequest(true);
    Mockito.doThrow(new NoSuchElementException("Emergency group not found")).when(emergencyGroupService).answerEmergencyGroupInvitation(groupId, true);

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/emergency-groups/answer-invitation/{groupId}", groupId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Emergency group not found."));

    Mockito.verify(emergencyGroupService, Mockito.times(1)).answerEmergencyGroupInvitation(groupId, true);
  }

  @Test
  @WithMockUser
  void answerInvitation_IllegalArgument() throws Exception {
    Long groupId = 101L;
    InvitationReplyRequest request = new InvitationReplyRequest(true);
    String errorMessage = "Invalid invitation response.";
    Mockito.doThrow(new IllegalArgumentException(errorMessage)).when(emergencyGroupService).answerEmergencyGroupInvitation(groupId, true);

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/emergency-groups/answer-invitation/{groupId}", groupId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));

    Mockito.verify(emergencyGroupService, Mockito.times(1)).answerEmergencyGroupInvitation(groupId, true);
  }

  @Test
  @WithMockUser
  void answerInvitation_UnexpectedError() throws Exception {
    Long groupId = 202L;
    InvitationReplyRequest request = new InvitationReplyRequest(false);
    String errorMessage = "Failed to update user status.";
    Mockito.doThrow(new RuntimeException(errorMessage)).when(emergencyGroupService).answerEmergencyGroupInvitation(groupId, false);

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/emergency-groups/answer-invitation/{groupId}", groupId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("An unexpected error occurred while answering the invitation."));

    Mockito.verify(emergencyGroupService, Mockito.times(1)).answerEmergencyGroupInvitation(groupId, false);
  }

  @Test
  @WithMockUser
  void answerInvitation_ValidationError() throws Exception {
    Long groupId = 303L;
    String invalidJson = "{\"isAccept\": null}";

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/emergency-groups/answer-invitation/{groupId}", groupId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").isNotEmpty());

    Mockito.verify(emergencyGroupService, Mockito.never()).answerEmergencyGroupInvitation(Mockito.anyLong(), Mockito.anyBoolean());
  }
}
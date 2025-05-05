package com.group7.krisefikser.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.model.HouseholdInvitation;
import com.group7.krisefikser.model.InvitationRequest;
import com.group7.krisefikser.service.HouseholdInvitationService;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HouseholdInvitationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HouseholdInvitationService invitationService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/household-invitations";
    private InvitationRequest testInvitationRequest;
    private HouseholdInvitation testInvitation;
    private final Long householdId = 1L;
    private final Long userId = 2L;
    private final String email = "test@example.com";
    private final String token = "test-invitation-token";

    @BeforeEach
    void setUp() {
        testInvitationRequest = new InvitationRequest();
        testInvitationRequest.setEmail(email);

        testInvitation = new HouseholdInvitation();
        testInvitation.setId(1L);
        testInvitation.setHouseholdId(householdId);
        testInvitation.setInvitedByUserId(userId);
        testInvitation.setInvitedEmail(email);
        testInvitation.setInvitationToken(token);
        testInvitation.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(username = "2", roles = "NORMAL")
    void createInvitation_validRequest_returnsOkWithInvitation() throws Exception {
        when(invitationService.createInvitation(
          anyLong(),
          anyString()
        )).thenReturn(testInvitation);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testInvitationRequest)))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testInvitation.getId()))
          .andExpect(MockMvcResultMatchers.jsonPath("$.householdId").value(householdId))
          .andExpect(MockMvcResultMatchers.jsonPath("$.invitedByUserId").value(userId))
          .andExpect(MockMvcResultMatchers.jsonPath("$.invitedEmail").value(email))
          .andExpect(MockMvcResultMatchers.jsonPath("$.invitationToken").value(token));
    }

    @Test
    @WithMockUser(username = "2", roles = "NORMAL")
    void verifyInvitation_validToken_returnsOkWithInvitation() throws Exception {
        when(invitationService.verifyInvitation(eq(token))).thenReturn(testInvitation);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/verify")
            .param("token", token))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testInvitation.getId()))
          .andExpect(MockMvcResultMatchers.jsonPath("$.householdId").value(householdId))
          .andExpect(MockMvcResultMatchers.jsonPath("$.invitedByUserId").value(userId))
          .andExpect(MockMvcResultMatchers.jsonPath("$.invitedEmail").value(email));
    }

    @Test
    @WithMockUser(username = "2", roles = "NORMAL")
    void verifyInvitation_missingToken_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/verify"))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "2", roles = "NORMAL")
    void acceptInvitation_validToken_returnsOk() throws Exception {
        when(invitationService.acceptInvitation(eq(token), eq(2L)))
          .thenReturn(householdId);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", token);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/accept")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
          .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "2", roles = "NORMAL")
    void acceptInvitation_missingToken_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/accept")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
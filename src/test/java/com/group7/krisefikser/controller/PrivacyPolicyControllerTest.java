package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.UpdateRegisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.request.UpdateUnregisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.response.GetRegisteredPrivacyPolicyResponse;
import com.group7.krisefikser.dto.response.GetUnregisteredPrivacyPolicyResponse;
import com.group7.krisefikser.service.PrivacyPolicyService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrivacyPolicyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PrivacyPolicyService privacyPolicyService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testGetRegisteredPrivacyPolicy() throws Exception {
    GetRegisteredPrivacyPolicyResponse mockResponse = new GetRegisteredPrivacyPolicyResponse();
    mockResponse.setRegistered("Some policy");
    Mockito.when(privacyPolicyService.getRegisteredPrivacyPolicy()).thenReturn(mockResponse);

    mockMvc.perform(get("/api/privacy-policy/registered"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.registered").value("Some policy"));
  }

  @Test
  void testGetUnregisteredPrivacyPolicy() throws Exception {
    GetUnregisteredPrivacyPolicyResponse mockResponse = new GetUnregisteredPrivacyPolicyResponse();
    mockResponse.setUnregistered("Other policy");
    Mockito.when(privacyPolicyService.getUnregisteredPrivacyPolicy()).thenReturn(mockResponse);

    mockMvc.perform(get("/api/privacy-policy/unregistered"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.unregistered").value("Other policy"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testUpdateRegisteredPrivacyPolicy() throws Exception {
    UpdateRegisteredPrivacyPolicyRequest request = new UpdateRegisteredPrivacyPolicyRequest();
    request.setRegistered("New registered policy");

    mockMvc.perform(post("/api/privacy-policy/registered")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Registered privacy policy updated successfully"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testUpdateUnregisteredPrivacyPolicy() throws Exception {
    UpdateUnregisteredPrivacyPolicyRequest request = new UpdateUnregisteredPrivacyPolicyRequest();
    request.setUnregistered("New unregistered policy");

    mockMvc.perform(post("/api/privacy-policy/unregistered")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Unregistered privacy policy updated successfully"));
  }
}

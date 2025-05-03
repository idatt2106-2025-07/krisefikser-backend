package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.InviteAdminRequest;
import com.group7.krisefikser.dto.request.RegisterAdminRequest;
import com.group7.krisefikser.service.AdminService;
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AdminService adminService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void invite_shouldReturnOk_whenServiceSucceeds() throws Exception {
    InviteAdminRequest request = new InviteAdminRequest();
    request.setEmail("admin@example.com");

    doNothing().when(adminService).inviteAdmin(request);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/invite")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Admin invited successfully"));
  }

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void invite_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
    InviteAdminRequest request = new InviteAdminRequest();
    request.setEmail("admin@example.com");

    doThrow(new IllegalArgumentException("Email already in use")).when(adminService).inviteAdmin(request);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/invite")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error inviting admin"));
  }

  @Test
  void register_shouldReturnOk_whenServiceSucceeds() throws Exception {
    RegisterAdminRequest request = new RegisterAdminRequest();
    request.setEmail("admin@example.com");
    request.setPassword("Password123*");
    request.setToken("token123");

    doNothing().when(adminService).registerAdmin(request);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Admin registered successfully"));
  }

  @Test
  void register_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
    RegisterAdminRequest request = new RegisterAdminRequest();
    request.setEmail("admin@example.com");
    request.setPassword("Password123*");
    request.setToken("token123");

    doThrow(new IllegalArgumentException("Username in use")).when(adminService).registerAdmin(request);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error registering admin"));
  }
}

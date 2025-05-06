package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.InviteAdminRequest;
import com.group7.krisefikser.dto.request.RegisterAdminRequest;
import com.group7.krisefikser.dto.request.TwoFactorLoginRequest;
import com.group7.krisefikser.service.AdminService;
import jakarta.servlet.http.HttpServletResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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

  @Test
  void twoFactorAuthentication_shouldReturnOk_whenServiceSucceeds() throws Exception {
    TwoFactorLoginRequest request = new TwoFactorLoginRequest();
    request.setToken("valid2FAToken");

    doNothing().when(adminService).verifyTwoFactor(eq("valid2FAToken"), any(HttpServletResponse.class));

    mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/2fa")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Two Factor Authentication verified successfully"));
  }

  @Test
  void twoFactorAuthentication_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
    TwoFactorLoginRequest request = new TwoFactorLoginRequest();
    request.setToken("invalid2FAToken");

    doThrow(new IllegalArgumentException("Invalid 2FA token"))
        .when(adminService).verifyTwoFactor(eq("invalid2FAToken"), any(HttpServletResponse.class));

    mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/2fa")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error verifying Two Factor Authentication"));
  }
}

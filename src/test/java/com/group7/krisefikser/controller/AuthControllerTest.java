package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.HouseholdRequest;
import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
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

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void registerUser_validRequest_returnsCreatedResponse() throws Exception {
    RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123",
      new HouseholdRequest("Test Household", 0.0, 0.0)  // Add the missing parameter
    );
    AuthResponse response = new AuthResponse("User registered successfully", new Date(), Role.ROLE_NORMAL);

    Mockito.when(userService.registerUser(any(RegisterRequest.class)))
        .thenReturn(response);

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("User registered successfully"))
        .andExpect(jsonPath("$.expiryDate").exists())
        .andExpect(jsonPath("$.role").value("ROLE_NORMAL"));
  }

  @Test
  void registerUser_serviceThrowsException_returnsInternalServerError() throws Exception {
    RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "secret",
      new HouseholdRequest("Test Household", 0.0, 0.0)  // Add the missing parameter
    );

    Mockito.when(userService.registerUser(any(RegisterRequest.class)))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(AuthResponseMessage.SAVING_USER_ERROR.getMessage() + "Database error"))
        .andExpect(jsonPath("$.expiryDate").doesNotExist())
        .andExpect(jsonPath("$.role").doesNotExist());
  }

  @Test
  void loginUser_validRequest_returnsOkResponse() throws Exception {
    LoginRequest request = new LoginRequest("john@example.com", "password123");
    AuthResponse response = new AuthResponse("User logged in successfully", new Date(), Role.ROLE_NORMAL);

    Mockito.when(userService.loginUser(any(LoginRequest.class), any(HttpServletResponse.class)))
        .thenReturn(response);

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("User logged in successfully"))
        .andExpect(jsonPath("$.expiryDate").exists())
        .andExpect(jsonPath("$.role").value("ROLE_NORMAL"));
  }

  @Test
  void loginUser_serviceThrowsException_returnsInternalServerError() throws Exception {
    LoginRequest request = new LoginRequest("jane@example.com", "wrongpassword");

    Mockito.when(userService.loginUser(any(LoginRequest.class), any(HttpServletResponse.class)))
        .thenThrow(new RuntimeException("Authentication failed"));

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(AuthResponseMessage.USER_LOGIN_ERROR.getMessage() + "Authentication failed"))
        .andExpect(jsonPath("$.expiryDate").doesNotExist())
        .andExpect(jsonPath("$.role").doesNotExist());
  }

  @WithMockUser
  @Test
  void verifyEmail_validToken_returnsOkResponse() throws Exception {
    String token = "valid-token";
    AuthResponse response = new AuthResponse("User verified successfully", null, null);

    Mockito.when(userService.verifyEmail(token)).thenReturn(response);

    mockMvc.perform(get("/api/auth/verify-email")
            .param("token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("User verified successfully"))
        .andExpect(jsonPath("$.expiryDate").doesNotExist())
        .andExpect(jsonPath("$.role").doesNotExist());
  }

  @WithMockUser
  @Test
  void verifyEmail_userNotFound_returnsBadRequest() throws Exception {
    String token = "valid-token";
    AuthResponse response = new AuthResponse("User not found", null, null);

    Mockito.when(userService.verifyEmail(token)).thenReturn(response);

    mockMvc.perform(get("/api/auth/verify-email")
            .param("token", token))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.expiryDate").doesNotExist())
        .andExpect(jsonPath("$.role").doesNotExist());
  }

  @WithMockUser
  @Test
  void verifyEmail_invalidToken_returnsInternalServerError() throws Exception {
    String token = "invalid-token";

    Mockito.when(userService.verifyEmail(token))
        .thenThrow(new RuntimeException("Invalid token"));

    mockMvc.perform(get("/api/auth/verify-email")
            .param("token", token))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(AuthResponseMessage.EMAIL_VERIFICATION_ERROR.getMessage() + "Invalid token"))
        .andExpect(jsonPath("$.expiryDate").doesNotExist())
        .andExpect(jsonPath("$.role").doesNotExist());
  }

  @WithMockUser(username = "johndoe@test.com", roles = "USER")
  @Test
  void getCurrentUserEmail_authenticated_returnsEmail() throws Exception {
    com.group7.krisefikser.model.User mockUser = new com.group7.krisefikser.model.User();
    mockUser.setEmail("johndoe@test.com");
    mockUser.setName("John Doe");
    Mockito.when(userService.getCurrentUser()).thenReturn(mockUser);

    mockMvc.perform(get("/api/auth/me"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.email").value("johndoe@test.com"));
  }

  @Test
  void getCurrentUserEmail_unauthenticated_returnsNoContent() throws Exception {
    mockMvc.perform(get("/api/auth/me"))
        .andExpect(status().isNoContent());
  }

  @WithMockUser
  @Test
  void logout_clearsJwtCookieAndReturnsOk() throws Exception {
    mockMvc.perform(post("/api/auth/logout"))
        .andExpect(status().isOk())
        .andExpect(cookie().exists("JWT"))
        .andExpect(cookie().maxAge("JWT", 0))
        .andExpect(cookie().httpOnly("JWT", true))
        .andExpect(cookie().secure("JWT", true))
        .andExpect(cookie().path("JWT", "/"));
  }
}
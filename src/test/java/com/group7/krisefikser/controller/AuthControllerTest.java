package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
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
    RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123");
    AuthResponse response = new AuthResponse("john@example.com", "User registered successfully", new Date(), 1L);

    Mockito.when(userService.registerUser(any(RegisterRequest.class), any(HttpServletResponse.class)))
        .thenReturn(response);

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("john@example.com"))
        .andExpect(jsonPath("$.message").value("User registered successfully"))
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void registerUser_serviceThrowsException_returnsInternalServerError() throws Exception {
    RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "secret");

    Mockito.when(userService.registerUser(any(RegisterRequest.class), any(HttpServletResponse.class)))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.email").value("jane@example.com"))
        .andExpect(jsonPath("$.message").value(AuthResponseMessage.SAVING_USER_ERROR.getMessage() + "Database error"))
        .andExpect(jsonPath("$.expiryDate").doesNotExist())
        .andExpect(jsonPath("$.id").doesNotExist());
  }

  @Test
  void loginUser_validRequest_returnsOkResponse() throws Exception {
    LoginRequest request = new LoginRequest("john@example.com", "password123");
    AuthResponse response = new AuthResponse("john@example.com", "Login successful", new Date(), 1L);

    Mockito.when(userService.loginUser(any(LoginRequest.class))).thenReturn(response);

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("john@example.com"))
        .andExpect(jsonPath("$.message").value("Login successful"))
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void loginUser_serviceThrowsException_returnsInternalServerError() throws Exception {
    LoginRequest request = new LoginRequest("jane@example.com", "wrongpassword");

    Mockito.when(userService.loginUser(any(LoginRequest.class)))
        .thenThrow(new RuntimeException("Authentication failed"));

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.email").value("jane@example.com"))
        .andExpect(jsonPath("$.message").value(AuthResponseMessage.USER_LOGIN_ERROR.getMessage() + "Authentication failed"))
        .andExpect(jsonPath("$.expiryDate").doesNotExist())
        .andExpect(jsonPath("$.id").doesNotExist());
  }
}
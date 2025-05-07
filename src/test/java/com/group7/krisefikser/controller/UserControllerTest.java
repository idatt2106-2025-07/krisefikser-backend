package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.response.UserInfoResponse;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired
  private WebApplicationContext context;

  @MockitoBean
  private UserService userService;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  @WithMockUser
  void getUserProfile_returnsUserInfoResponse() throws Exception {
    UserInfoResponse mockResponse = new UserInfoResponse(
        "john.doe@example.com", "John Doe", Role.ROLE_NORMAL, 10, 11
    );

    when(userService.getUserInfo()).thenReturn(mockResponse);

    mockMvc.perform(get("/api/user/profile")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("john.doe@example.com"))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.role").value("ROLE_NORMAL"))
        .andExpect(jsonPath("$.householdLongitude").value("11.0"))
        .andExpect(jsonPath("$.householdLatitude").value("10.0"));
  }

  @Test
  @WithMockUser
  void getUserProfile_returnsInternalServerError_whenServiceFails() throws Exception {
    when(userService.getUserInfo()).thenThrow(new RuntimeException("Something went wrong"));

    mockMvc.perform(get("/api/user/profile")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error fetching user profile"));
  }
}

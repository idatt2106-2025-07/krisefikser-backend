package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.InviteAdminRequest;
import com.group7.krisefikser.dto.response.SuperAdminResponse;
import com.group7.krisefikser.service.SuperAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SuperAdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SuperAdminService superAdminService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void invite_shouldReturnOk_whenServiceSucceeds() throws Exception {
    InviteAdminRequest request = new InviteAdminRequest();
    request.setEmail("admin@example.com");

    doNothing().when(superAdminService).inviteAdmin(request);

    mockMvc.perform(post("/api/super-admin/invite")
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

    doThrow(new IllegalArgumentException("Email already in use")).when(superAdminService).inviteAdmin(request);

    mockMvc.perform(post("/api/super-admin/invite")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error inviting admin"));
  }

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void getAllAdmins_ShouldReturnListOfAdmins_WhenServiceReturnsAdmins() throws Exception {
    SuperAdminResponse admin1 = new SuperAdminResponse("admin1@test.com");
    SuperAdminResponse admin2 = new SuperAdminResponse("admin2@test.com");

    when(superAdminService.getAllAdmins()).thenReturn(List.of(admin1, admin2));

    mockMvc.perform(get("/api/super-admin/admins"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].email").value("admin1@test.com"));
  }

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void getAllAdmins_ShouldReturn500_WhenServiceThrowsException() throws Exception {
    when(superAdminService.getAllAdmins()).thenThrow(new RuntimeException("Something went wrong"));

    mockMvc.perform(get("/api/super-admin/admins"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void deleteAdmin_ShouldReturn204_WhenAdminIsDeleted() throws Exception {
    mockMvc.perform(delete("/api/super-admin/delete/{adminId}", 1L))
        .andExpect(status().isNoContent());

    verify(superAdminService).deleteAdmin(1L);
  }

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void deleteAdmin_ShouldReturn500_WhenServiceThrowsException() throws Exception {
    doThrow(new RuntimeException("Delete failed")).when(superAdminService).deleteAdmin(1L);

    mockMvc.perform(delete("/api/super-admin/delete/{adminId}", 1L))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void sendNewPasswordLink_ShouldReturn200_WhenLinkIsSent() throws Exception {
    String email = "admin@test.com";

    mockMvc.perform(post("/api/super-admin/admins/new-password-link")
            .contentType(MediaType.APPLICATION_JSON)
            .content(email))
        .andExpect(status().isOk())
        .andExpect(content().string("New password link sent to: " + email));

    verify(superAdminService).sendResetPasswordEmailToAdmin(email);
  }

  @Test
  @WithMockUser(roles = "SUPER_ADMIN")
  void sendNewPasswordLink_ShouldReturn500_WhenServiceThrowsException() throws Exception {
    String email = "admin@test.com";

    doThrow(new RuntimeException("Email failed")).when(superAdminService).sendResetPasswordEmailToAdmin(email);

    mockMvc.perform(post("/api/super-admin/admins/new-password-link")
            .contentType(MediaType.APPLICATION_JSON)
            .content(email))
        .andExpect(status().isInternalServerError());
  }
}

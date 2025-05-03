package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.GeneralInfoRequest;
import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.model.GeneralInfo;
import com.group7.krisefikser.service.GeneralInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GeneralInfoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GeneralInfoService generalInfoService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void getAllGeneralInfo_shouldReturnList() throws Exception {
    GeneralInfo info = new GeneralInfo();
    info.setId(1L);
    info.setTheme(Theme.BEFORE_CRISIS);
    info.setTitle("Test");
    info.setContent("Content");

    Mockito.when(generalInfoService.getAllGeneralInfo()).thenReturn(List.of(info));

    mockMvc.perform(get("/api/general-info/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Test"));
  }

  @Test
  void getGeneralInfoByTheme_shouldReturnList() throws Exception {
    GeneralInfo info = new GeneralInfo();
    info.setTheme(Theme.AFTER_CRISIS);
    info.setTitle("Title");
    info.setContent("Content");

    Mockito.when(generalInfoService.getGeneralInfoByTheme(Theme.AFTER_CRISIS))
        .thenReturn(List.of(info));

    mockMvc.perform(get("/api/general-info/after_crisis"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].theme").value("AFTER_CRISIS"));
  }

  @Test
  void getGeneralInfoByTheme_shouldReturnBadRequestForInvalidTheme() throws Exception {
    mockMvc.perform(get("/api/general-info/invalid_theme"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void addGeneralInfo_shouldCallService() throws Exception {
    GeneralInfoRequest request = new GeneralInfoRequest();
    request.setTheme("before_crisis");
    request.setTitle("Some Title");
    request.setContent("Some Content");

    mockMvc.perform(post("/api/general-info/admin/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    Mockito.verify(generalInfoService).addGeneralInfo(any(GeneralInfoRequest.class));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void updateGeneralInfo_shouldCallServiceWithId() throws Exception {
    GeneralInfoRequest request = new GeneralInfoRequest();
    request.setTheme("during_crisis");
    request.setTitle("Update Title");
    request.setContent("Updated Content");

    mockMvc.perform(put("/api/general-info/admin/update/42")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    Mockito.verify(generalInfoService).updateGeneralInfo(any(GeneralInfoRequest.class), eq(42L));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void deleteGeneralInfo_shouldCallServiceWithId() throws Exception {
    mockMvc.perform(delete("/api/general-info/admin/delete/5"))
        .andExpect(status().isOk());

    Mockito.verify(generalInfoService).deleteGeneralInfo(5L);
  }
}
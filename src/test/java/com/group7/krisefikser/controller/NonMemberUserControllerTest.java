package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.AddNonUserMemberRequest;
import com.group7.krisefikser.dto.request.DeleteNonUserMemberRequest;
import com.group7.krisefikser.dto.request.UpdateNonUserMemberRequest;
import com.group7.krisefikser.enums.NonUserMemberType;
import com.group7.krisefikser.service.NonUserMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class NonMemberUserControllerTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @MockBean
  private NonUserMemberService nonUserMemberService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void addNonUserMember_returnsOk() throws Exception {
    AddNonUserMemberRequest request = new AddNonUserMemberRequest();
    request.setName("Test Member");
    request.setType(NonUserMemberType.CHILD);

    mockMvc.perform(post("/api/non-user-member/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Non-user member added successfully"));
  }

  @Test
  void addNonUserMember_returnsServerError() throws Exception {
    AddNonUserMemberRequest request = new AddNonUserMemberRequest();
    request.setName("Error Member");
    request.setType(NonUserMemberType.CHILD);

    Mockito.doThrow(new RuntimeException("Add error"))
        .when(nonUserMemberService).addNonUserMember(request);

    mockMvc.perform(post("/api/non-user-member/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error adding non-user member"));
  }

  @Test
  void updateNonUserMember_returnsOk() throws Exception {
    UpdateNonUserMemberRequest request = new UpdateNonUserMemberRequest();
    request.setId(1L);
    request.setName("Updated Name");

    mockMvc.perform(post("/api/non-user-member/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Non-user member updated successfully"));
  }

  @Test
  void updateNonUserMember_returnsServerError() throws Exception {
    UpdateNonUserMemberRequest request = new UpdateNonUserMemberRequest();
    request.setId(99L);
    request.setName("Will Fail");
    request.setType(NonUserMemberType.CHILD.toString());

    doThrow(new RuntimeException("Update failed"))
        .when(nonUserMemberService).updateNonUserMember(request);

    mockMvc.perform(post("/api/non-user-member/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error updating non-user member"));
  }

  @Test
  void deleteNonUserMember_returnsOk() throws Exception {
    DeleteNonUserMemberRequest request = new DeleteNonUserMemberRequest();
    request.setId(1L);

    mockMvc.perform(delete("/api/non-user-member/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Non-user member deleted successfully"));
  }

  @Test
  void deleteNonUserMember_returnsServerError() throws Exception {
    DeleteNonUserMemberRequest request = new DeleteNonUserMemberRequest();
    request.setId(404L);

    doThrow(new RuntimeException("Delete failed"))
        .when(nonUserMemberService).deleteNonUserMember(request);

    mockMvc.perform(delete("/api/non-user-member/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error deleting non-user member"));
  }
}

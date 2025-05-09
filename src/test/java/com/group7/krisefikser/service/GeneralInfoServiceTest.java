package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.article.GeneralInfoRequest;
import com.group7.krisefikser.dto.response.article.GeneralInfoResponse;
import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.model.article.GeneralInfo;
import com.group7.krisefikser.repository.article.GeneralInfoRepository;
import com.group7.krisefikser.service.article.GeneralInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeneralInfoServiceTest {

  @Mock
  private GeneralInfoRepository generalInfoRepo;

  @InjectMocks
  private GeneralInfoService generalInfoService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getAllGeneralInfo_shouldReturnList() {
    GeneralInfo info = new GeneralInfo();
    info.setId(1L);
    info.setTheme(Theme.BEFORE_CRISIS);
    info.setTitle("Test Title");
    info.setContent("Test Content");

    when(generalInfoRepo.getAllGeneralInfo()).thenReturn(List.of(info));

    List<GeneralInfoResponse> result = generalInfoService.getAllGeneralInfo();
    assertEquals(1, result.size());
    assertEquals("Test Title", result.get(0).getTitle());
  }

  @Test
  void getGeneralInfoByTheme_shouldReturnListForTheme() {
    Theme theme = Theme.DURING_CRISIS;

    when(generalInfoRepo.getGeneralInfoByTheme(theme)).thenReturn(Collections.emptyList());

    List<GeneralInfoResponse> result = generalInfoService.getGeneralInfoByTheme(theme);
    assertTrue(result.isEmpty());
  }

  @Test
  void addGeneralInfo_shouldCallRepositoryWithMappedObject() {
    GeneralInfoRequest request = new GeneralInfoRequest();
    request.setTheme("after_crisis");
    request.setTitle("Title");
    request.setContent("Content");

    GeneralInfoService service = new GeneralInfoService(generalInfoRepo);
    service.addGeneralInfo(request);

    verify(generalInfoRepo).addGeneralInfo(any(GeneralInfo.class));
  }

  @Test
  void updateGeneralInfo_shouldCallRepositoryWithMappedObjectAndId() {
    GeneralInfoRequest request = new GeneralInfoRequest();
    request.setTheme("before_crisis");
    request.setTitle("Updated");
    request.setContent("Updated content");

    generalInfoService.updateGeneralInfo(request, 5L);
    verify(generalInfoRepo).updateGeneralInfo(any(GeneralInfo.class), eq(5L));
  }

  @Test
  void deleteGeneralInfo_shouldCallRepositoryWithId() {
    generalInfoService.deleteGeneralInfo(10L);
    verify(generalInfoRepo).deleteGeneralInfo(10L);
  }
}
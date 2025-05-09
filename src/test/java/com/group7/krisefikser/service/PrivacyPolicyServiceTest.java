package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.article.UpdateRegisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.request.article.UpdateUnregisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.response.article.GetRegisteredPrivacyPolicyResponse;
import com.group7.krisefikser.dto.response.article.GetUnregisteredPrivacyPolicyResponse;
import com.group7.krisefikser.repository.article.PrivacyPolicyRepository;
import com.group7.krisefikser.service.article.PrivacyPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PrivacyPolicyServiceTest {

  private PrivacyPolicyRepository privacyPolicyRepository;
  private PrivacyPolicyService service;

  @BeforeEach
  void setUp() {
    privacyPolicyRepository = Mockito.mock(PrivacyPolicyRepository.class);
    service = new PrivacyPolicyService(privacyPolicyRepository);
  }

  @Test
  void testGetRegisteredPrivacyPolicy() {
    when(privacyPolicyRepository.getRegisteredPrivacyPolicy()).thenReturn("Mock Registered");

    GetRegisteredPrivacyPolicyResponse response = service.getRegisteredPrivacyPolicy();

    assertNotNull(response);
    assertEquals("Mock Registered", response.getRegistered());
    verify(privacyPolicyRepository, times(1)).getRegisteredPrivacyPolicy();
  }

  @Test
  void testGetUnregisteredPrivacyPolicy() {
    when(privacyPolicyRepository.getUnregisteredPrivacyPolicy()).thenReturn("Mock Unregistered");

    GetUnregisteredPrivacyPolicyResponse response = service.getUnregisteredPrivacyPolicy();

    assertNotNull(response);
    assertEquals("Mock Unregistered", response.getUnregistered());
    verify(privacyPolicyRepository, times(1)).getUnregisteredPrivacyPolicy();
  }

  @Test
  void testUpdateRegisteredPrivacyPolicy() {
    UpdateRegisteredPrivacyPolicyRequest request = new UpdateRegisteredPrivacyPolicyRequest();
    request.setRegistered("Updated Registered Policy");

    service.updateRegisteredPrivacyPolicy(request);

    verify(privacyPolicyRepository, times(1)).updateRegisteredPrivacyPolicy("Updated Registered Policy");
  }

  @Test
  void testUpdateUnregisteredPrivacyPolicy() {
    UpdateUnregisteredPrivacyPolicyRequest request = new UpdateUnregisteredPrivacyPolicyRequest();
    request.setUnregistered("Updated Unregistered Policy");

    service.updateUnregisteredPrivacyPolicy(request);

    verify(privacyPolicyRepository, times(1)).updateUnregisteredPrivacyPolicy("Updated Unregistered Policy");
  }
}

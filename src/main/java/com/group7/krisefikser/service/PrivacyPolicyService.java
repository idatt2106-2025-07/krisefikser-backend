package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.UpdateRegisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.request.UpdateUnregisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.response.GetRegisteredPrivacyPolicyResponse;
import com.group7.krisefikser.dto.response.GetUnregisteredPrivacyPolicyResponse;
import com.group7.krisefikser.repository.PrivacyPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrivacyPolicyService {

  private final PrivacyPolicyRepository privacyPolicyRepository;

  public GetRegisteredPrivacyPolicyResponse getRegisteredPrivacyPolicy() {
    GetRegisteredPrivacyPolicyResponse response = new GetRegisteredPrivacyPolicyResponse();
    response.setRegistered(privacyPolicyRepository.getRegisteredPrivacyPolicy());
    return response;
  }

  public GetUnregisteredPrivacyPolicyResponse getUnregisteredPrivacyPolicy() {
    GetUnregisteredPrivacyPolicyResponse response = new GetUnregisteredPrivacyPolicyResponse();
    response.setUnregistered(privacyPolicyRepository.getUnregisteredPrivacyPolicy());
    return response;
  }

  public void updateRegisteredPrivacyPolicy(UpdateRegisteredPrivacyPolicyRequest request) {
    privacyPolicyRepository.updateRegisteredPrivacyPolicy(request.getRegistered());
  }

  public void updateUnregisteredPrivacyPolicy(UpdateUnregisteredPrivacyPolicyRequest request) {
    privacyPolicyRepository.updateUnregisteredPrivacyPolicy(request.getUnregistered());
  }
}

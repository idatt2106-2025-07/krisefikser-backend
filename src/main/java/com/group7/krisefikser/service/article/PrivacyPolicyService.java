package com.group7.krisefikser.service.article;

import com.group7.krisefikser.dto.request.article.UpdateRegisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.request.article.UpdateUnregisteredPrivacyPolicyRequest;
import com.group7.krisefikser.dto.response.article.GetRegisteredPrivacyPolicyResponse;
import com.group7.krisefikser.dto.response.article.GetUnregisteredPrivacyPolicyResponse;
import com.group7.krisefikser.repository.article.PrivacyPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing privacy policy data.
 */
@Service
@RequiredArgsConstructor
public class PrivacyPolicyService {

  private final PrivacyPolicyRepository privacyPolicyRepository;

  /**
   * Retrieves the registered privacy policy.
   *
   * @return The registered privacy policy as a response object.
   */
  public GetRegisteredPrivacyPolicyResponse getRegisteredPrivacyPolicy() {
    GetRegisteredPrivacyPolicyResponse response = new GetRegisteredPrivacyPolicyResponse();
    response.setRegistered(privacyPolicyRepository.getRegisteredPrivacyPolicy());
    return response;
  }

  /**
   * Retrieves the unregistered privacy policy.
   *
   * @return The unregistered privacy policy as a response object.
   */
  public GetUnregisteredPrivacyPolicyResponse getUnregisteredPrivacyPolicy() {
    GetUnregisteredPrivacyPolicyResponse response = new GetUnregisteredPrivacyPolicyResponse();
    response.setUnregistered(privacyPolicyRepository.getUnregisteredPrivacyPolicy());
    return response;
  }

  /**
   * Updates the registered privacy policy.
   *
   * @param request The request object containing the new registered privacy policy.
   */
  public void updateRegisteredPrivacyPolicy(UpdateRegisteredPrivacyPolicyRequest request) {
    privacyPolicyRepository.updateRegisteredPrivacyPolicy(request.getRegistered());
  }

  /**
   * Updates the unregistered privacy policy.
   *
   * @param request The request object containing the new unregistered privacy policy.
   */
  public void updateUnregisteredPrivacyPolicy(UpdateUnregisteredPrivacyPolicyRequest request) {
    privacyPolicyRepository.updateUnregisteredPrivacyPolicy(request.getUnregistered());
  }
}

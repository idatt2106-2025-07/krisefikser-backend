package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.AddNonUserMemberRequest;
import com.group7.krisefikser.dto.request.DeleteNonUserMemberRequest;
import com.group7.krisefikser.dto.request.UpdateNonUserMemberRequest;
import com.group7.krisefikser.mapper.NonUserMemberMapper;
import com.group7.krisefikser.model.NonUserMember;
import com.group7.krisefikser.repository.NonUserMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing non-user members in a household.
 * This class provides methods to add, update, and delete non-user members.
 */
@Service
@RequiredArgsConstructor
public class NonUserMemberService {

  private final NonUserMemberRepository nonUserMemberRepository;
  private final UserService userService;

  /**
   * Adds a non-user member to the household.
   *
   * @param request the request object containing the details of the non-user member to be added
   */
  public void addNonUserMember(AddNonUserMemberRequest request) {
    NonUserMember nonUserMember =
        NonUserMemberMapper.INSTANCE.addNonUserMemberRequestToNonUserMember(request);
    long householdId = userService.getCurrentUserHouseholdId();
    nonUserMember.setHouseholdId(householdId);

    nonUserMemberRepository.addNonUserMember(nonUserMember);
  }

  /**
   * Updates an existing non-user member in the household.
   *
   * @param request the request object containing the updated details of the non-user member
   */
  public void updateNonUserMember(UpdateNonUserMemberRequest request) {
    NonUserMember nonUserMember =
        NonUserMemberMapper.INSTANCE.updateNonUserMemberRequestToNonUserMember(request);
    long householdId = userService.getCurrentUserHouseholdId();
    nonUserMember.setHouseholdId(householdId);

    nonUserMemberRepository.updateNonUserMember(nonUserMember);
  }

  /**
   * Deletes a non-user member from the household.
   *
   * @param request the request object containing the ID of the non-user member to be deleted
   */
  public void deleteNonUserMember(DeleteNonUserMemberRequest request) {
    long householdId = userService.getCurrentUserHouseholdId();
    nonUserMemberRepository.deleteNonUserMember(request.getId(), householdId);
  }
}

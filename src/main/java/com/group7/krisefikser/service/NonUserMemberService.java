package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.AddNonUserMemberRequest;
import com.group7.krisefikser.dto.request.DeleteNonUserMemberRequest;
import com.group7.krisefikser.dto.request.UpdateNonUserMemberRequest;
import com.group7.krisefikser.mapper.NonUserMemberMapper;
import com.group7.krisefikser.model.NonUserMember;
import com.group7.krisefikser.repository.NonUserMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NonUserMemberService {

  private final NonUserMemberRepository nonUserMemberRepository;
  private final UserService userService;

  public void addNonUserMember(AddNonUserMemberRequest request) {
    NonUserMember nonUserMember =
        NonUserMemberMapper.INSTANCE.addNonUserMemberRequestToNonUserMember(request);
    long householdId = userService.getCurrentUserHouseholdId();
    nonUserMember.setHouseholdId(householdId);

    nonUserMemberRepository.addNonUserMember(nonUserMember);
  }

  public void updateNonUserMember(UpdateNonUserMemberRequest request) {
    NonUserMember nonUserMember =
        NonUserMemberMapper.INSTANCE.updateNonUserMemberRequestToNonUserMember(request);
    long householdId = userService.getCurrentUserHouseholdId();
    nonUserMember.setHouseholdId(householdId);

    nonUserMemberRepository.updateNonUserMember(nonUserMember);
  }

  public void deleteNonUserMember(DeleteNonUserMemberRequest request) {
    long householdId = userService.getCurrentUserHouseholdId();
    nonUserMemberRepository.deleteNonUserMember(request.getId(), householdId);
  }
}

package com.group7.krisefikser.repository;

import com.group7.krisefikser.enums.NonUserMemberType;
import com.group7.krisefikser.model.household.NonUserMember;
import com.group7.krisefikser.repository.household.NonUserMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NonUserMemberRepositoryTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private NonUserMemberRepository repository;

  private long householdId;

  @BeforeEach
  void setup() {
    jdbcTemplate.update("DELETE FROM non_user_members");
    householdId = 1L;
  }

  @Test
  void addNonUserMember_savesToDatabase() {
    NonUserMember member = new NonUserMember();
    member.setName("John");
    member.setType(NonUserMemberType.CHILD);
    member.setHouseholdId(householdId);

    repository.addNonUserMember(member);

    List<NonUserMember> result = repository.getNonUserMembersByHousehold(householdId);
    assertEquals(1, result.size());
    assertEquals("John", result.get(0).getName());
    assertEquals(NonUserMemberType.CHILD, result.get(0).getType());
  }

  @Test
  void updateNonUserMember_updatesDatabaseRecord() {
    jdbcTemplate.update("INSERT INTO non_user_members (name, type, household_id) VALUES (?, ?, ?)",
        "John", "CHILD", householdId);

    NonUserMember updated = new NonUserMember();
    updated.setName("Johnny");
    updated.setType(NonUserMemberType.CHILD);
    updated.setHouseholdId(householdId);
    updated.setId(1L);
    repository.updateNonUserMember(updated);

    List<NonUserMember> result = repository.getNonUserMembersByHousehold(householdId);
    assertEquals(1, result.size());
    assertEquals("John", result.get(0).getName());
    assertEquals(NonUserMemberType.CHILD, result.get(0).getType());
  }

  @Test
  void deleteNonUserMember_removesFromDatabase() {
    jdbcTemplate.update("INSERT INTO non_user_members (id, name, type, household_id) VALUES (?, ?, ?, ?)",
        1L, "Jane", "CHILD", householdId);

    repository.deleteNonUserMember(1L, householdId);

    List<NonUserMember> result = repository.getNonUserMembersByHousehold(householdId);
    assertTrue(result.isEmpty());
  }

  @Test
  void getNonUserMembersByHousehold_returnsCorrectData() {
    jdbcTemplate.update("INSERT INTO non_user_members (name, type, household_id) VALUES (?, ?, ?)",
        "Alice", "CHILD", householdId);
    jdbcTemplate.update("INSERT INTO non_user_members (name, type, household_id) VALUES (?, ?, ?)",
        "Bob", "CHILD", householdId);

    List<NonUserMember> result = repository.getNonUserMembersByHousehold(householdId);
    assertEquals(2, result.size());
    assertEquals("Alice", result.get(0).getName());
    assertEquals("Bob", result.get(1).getName());
  }
}

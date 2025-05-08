package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.NonUserMember;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NonUserMemberRepository {

  private final JdbcTemplate jdbcTemplate;

  public void addNonUserMember(NonUserMember nonUserMember) {
    String sql = "INSERT INTO non_user_members (name, type, household_id) VALUES (?, ?, ?)";
    jdbcTemplate.update(
        sql,
        nonUserMember.getName(),
        nonUserMember.getType().toString(),
        nonUserMember.getHouseholdId());
  }

  public void updateNonUserMember(NonUserMember nonUserMember) {
    String sql = "UPDATE non_user_members SET type = ?, name = ? WHERE id = ? AND household_id = ?";
    jdbcTemplate.update(
        sql,
        nonUserMember.getType().toString(),
        nonUserMember.getName(),
        nonUserMember.getId(),
        nonUserMember.getHouseholdId());
  }

  public void deleteNonUserMember(long id, long householdId) {
    String sql = "DELETE FROM non_user_members WHERE id = ? AND household_id = ?";
    jdbcTemplate.update(sql, id, householdId);
  }

  /**
   * Retrieves a list of non-user members associated with a specific household.
   *
   * @param householdId The ID of the household for which to retrieve non-user members.
   * @return A list of NonUserMember objects associated with the specified household.
   */
  public List<NonUserMember> getNonUserMembersByHousehold(long householdId) {
    String sql = "SELECT * FROM non_user_members WHERE household_id = ?";

    return jdbcTemplate.query(
        sql,
        new Object[] {householdId},
        (rs, rowNum) -> {
          NonUserMember member = new NonUserMember();
          member.setId(rs.getLong("id"));
          member.setName(rs.getString("name"));
          member.setHouseholdId(rs.getLong("household_id"));
          member.setType(NonUserMemberType.valueOf(rs.getString("type").toUpperCase()));
          return member;
        });
  }
}

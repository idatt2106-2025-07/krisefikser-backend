package com.group7.krisefikser.repository;

import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void findByEmail_existingUser_returnsUser() {
    Optional<User> result = userRepository.findByEmail("user@example.com");

    assertTrue(result.isPresent());
    User user = result.get();
    assertEquals("user@example.com", user.getEmail());
    assertEquals("Bob User", user.getName());
    assertEquals(1L, user.getHouseholdId());
    assertEquals(Role.ROLE_NORMAL, user.getRole());
  }

  @Test
  void findByEmail_nonExistingUser_returnsEmpty() {
    Optional<User> result = userRepository.findByEmail("nonexistent@example.com");
    assertTrue(result.isEmpty());
  }

  @Test
  void save_validUser_savesAndReturnsUser() {
    User user = new User();
    user.setEmail("newuser@example.com");
    user.setName("New User");
    user.setPassword("securepassword");
    user.setHouseholdId(1L);

    Optional<User> savedUser = userRepository.save(user);

    assertTrue(savedUser.isPresent());
    assertEquals("newuser@example.com", savedUser.get().getEmail());
    assertEquals(Role.ROLE_NORMAL, savedUser.get().getRole());
  }

  @Test
  void save_userWithNullEmail_failsAndReturnsEmpty() {
    User user = new User();
    user.setName("Broken User");
    user.setPassword("password");
    user.setHouseholdId(1L);

    Optional<User> result = userRepository.save(user);
    assertTrue(result.isEmpty());
  }
}
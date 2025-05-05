package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

  private UserRepository userRepository;
  private UserService userService;

  public List<User> getAllAdmins() {
    try {
      List<User> admins = userRepository.findByRole(Role.ROLE_ADMIN);
      if (admins.isEmpty()) {
        throw new IllegalArgumentException("No admins found");
      }
      return admins;
    } catch (Exception e) {
      throw new RuntimeException("Error fetching admins: " + e.getMessage());
    }
  }

  public void deleteAdmin(Long adminId) {
    try {
      Optional<User> user = userRepository.findById(adminId);
      if (user.isPresent()) {
        if (!user.get().getRole().equals(Role.ROLE_ADMIN)) {
          throw new IllegalArgumentException("User is not an admin");
        }
        userRepository.deleteById(user.get().getId());
      } else {
        throw new IllegalArgumentException("User not found");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error deleting admin: " + e.getMessage());
    }
  }

  public AuthResponse sendResetPasswordEmailToAmdmin(String email) {
    try {
      Optional<User> user = userRepository.findByEmail(email);
      if (user.isPresent()) {
        if (!user.get().getRole().equals(Role.ROLE_ADMIN)) {
          throw new IllegalArgumentException("User is not an admin");
        }
        userService.sendResetPasswordLink(email);
        return new AuthResponse(
            AuthResponseMessage.PASSWORD_RESET_LINK_SENT.getMessage(),
            null,
            user.get().getRole()
        );
      }
      return new AuthResponse(
          AuthResponseMessage.USER_NOT_FOUND.getMessage(),
          null,
          null
      );
    } catch (Exception e) {
      return new AuthResponse(
          AuthResponseMessage.PASSWORD_RESET_LINK_REJECTED.getMessage() + e.getMessage(),
          null,
          null
      );
    }
  }
}

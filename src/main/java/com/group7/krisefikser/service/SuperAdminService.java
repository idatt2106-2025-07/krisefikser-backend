package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.dto.response.SuperAdminResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.ItemRepo;
import com.group7.krisefikser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

  private UserRepository userRepository;
  private UserService userService;

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

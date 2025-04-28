package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.InviteAdminRequest;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.utils.JwtUtils;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final EmailService emailService;

  private final JwtUtils jwtUtils;

  public void inviteAdmin(InviteAdminRequest request) throws JwtMissingPropertyException {
    String username = "admin" + generateShortenedUuid();

    String inviteToken = jwtUtils.generateInviteToken(username);

    String inviteLink = "https://localhost:5173/invite?token=" + inviteToken;

    emailService.sendTemplateMessage(
        request.getEmail(),
        EmailTemplateType.ADMIN_INVITE,
        Map.of("inviteLink", inviteLink)
    );
  }

  private String generateShortenedUuid() {
    UUID uuid = UUID.randomUUID();

    return Base64.getEncoder()
        .encodeToString(uuid.toString().getBytes())
        .replaceAll("[=+/]", "")
        .substring(0, 8);
  }
}

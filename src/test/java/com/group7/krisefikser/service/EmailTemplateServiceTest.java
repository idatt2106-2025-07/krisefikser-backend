package com.group7.krisefikser.service;

import com.group7.krisefikser.enums.EmailTemplateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailTemplateServiceTest {
  private EmailTemplateService emailTemplateService;

  @BeforeEach
  void setUp() {
    emailTemplateService = new EmailTemplateService();
  }

  @Test
  void testGetSubject_passwordReset() {
    String subject = emailTemplateService.getSubject(EmailTemplateType.PASSWORD_RESET);
    assertEquals("Password Reset Request", subject);
  }

  @Test
  void testGetSubject_householdInvite() {
    String subject = emailTemplateService.getSubject(EmailTemplateType.HOUSEHOLD_INVITE);
    assertEquals("Household Invite", subject);
  }

  @Test
  void testGetSubject_adminInvite() {
    String subject = emailTemplateService.getSubject(EmailTemplateType.ADMIN_INVITE);
    assertEquals("Admin Invite", subject);
  }

  @Test
  void testGetSubject_adminVerification() {
    String subject = emailTemplateService.getSubject(EmailTemplateType.ADMIN_VERIFICATION);
    assertEquals("Admin Verification", subject);
  }

  @Test
  void testGetBody_passwordReset() {
    Map<String, String> params = Map.of("resetLink", "http://example.com/reset");
    String body = emailTemplateService.getBody(EmailTemplateType.PASSWORD_RESET, params);
    assertEquals("Click the link below to reset your password:\nhttp://example.com/reset", body);
  }

  @Test
  void testGetBody_householdInvite() {
    Map<String, String> params = Map.of("inviteLink", "http://example.com/household");
    String body = emailTemplateService.getBody(EmailTemplateType.HOUSEHOLD_INVITE, params);
    assertEquals("You have been invited to join a household. Click the link below to accept the invitation:\nhttp://example.com/household", body);
  }

  @Test
  void testGetBody_adminInvite() {
    Map<String, String> params = Map.of("inviteLink", "http://example.com/admininvite");
    String body = emailTemplateService.getBody(EmailTemplateType.ADMIN_INVITE, params);
    assertEquals("You have been invited to become an admin. Click the link below to accept the invitation:\nhttp://example.com/admininvite", body);
  }

  @Test
  void testGetBody_adminVerification() {
    Map<String, String> params = Map.of("loginLink", "http://example.com/login");
    String body = emailTemplateService.getBody(EmailTemplateType.ADMIN_VERIFICATION, params);
    assertEquals("Your admin account has been verified. You can now log in.\nhttp://example.com/login", body);
  }
}

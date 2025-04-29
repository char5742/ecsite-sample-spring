package com.example.ec_2024b_back.auth.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.ec_2024b_back.share.domain.models.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmailAuthenticationTest {

  @Test
  void testValidEmailAuthenticationCreation() {
    var email = new Email("test@example.com");
    var password =
        new EmailAuthentication.HashedPassword(
            "$2a$10$abcdefghijklmnopqrstuvwxyno12345678901234567890123abc"); // 53文字に修正

    var auth = new EmailAuthentication(email, password);

    assertEquals(email, auth.email());
    assertEquals(password, auth.password());
  }

  @Test
  void testAuthenticationType() {
    var email = new Email("test@example.com");
    var password =
        new EmailAuthentication.HashedPassword(
            "$2a$10$abcdefghijklmnopqrstuvwxyno12345678901234567890123abc"); // 53文字に修正

    var auth = new EmailAuthentication(email, password);

    assertEquals("email", auth.type());
  }

  @Test
  void testHashedPasswordValidPattern() {
    // Valid bcrypt hash (53 characters after $2a$10$)
    var validHash = "$2a$10$abcdefghijklmnopqrstuvwxyno12345678901234567890123abc"; // 53文字に修正
    var password = new EmailAuthentication.HashedPassword(validHash);
    assertEquals(validHash, password.value());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "plain-text-password", // Not a bcrypt hash
        "$2c$10$abcdefghijklmnopqrstuvwxyno1234567890123456789012o", // Invalid version
        "$2a$10$short", // Too short
        "$2a$10$abcdefghijklmnopqrstuvwxyno123456789012345678901234", // 54 chars instead of 53
        "extra$2a$10$abcdefghijklmnopqrstuvwxyno1234567890123456789012o" // Extra prefix
      })
  void testHashedPasswordInvalidPattern(String invalidHash) {
    var exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new EmailAuthentication.HashedPassword(invalidHash),
            "Should reject invalid hash: " + invalidHash);
    assertEquals("パスワードは bcrypt 形式でなければなりません", exception.getMessage());
  }
}

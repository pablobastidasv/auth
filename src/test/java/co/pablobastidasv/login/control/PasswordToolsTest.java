package co.pablobastidasv.login.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordToolsTest {

  private PasswordTools passwordTools;
  private String password = "SuperSecretPassword!";
  private String wrongPassword = "ThisIsNotThePassword!";

  @BeforeEach
  void beforeAll() {
    passwordTools = new PasswordTools();
  }

  @Test
  void generateSalt() {
    Optional<String> salt1 = passwordTools.generateSalt(512);
    Optional<String> salt2 = passwordTools.generateSalt(512);
    Optional<String> salt3 = passwordTools.generateSalt(0);

    assertTrue(salt1.isPresent());
    assertTrue(salt2.isPresent());
    assertFalse(salt3.isPresent());

    assertNotEquals(salt1.get(), salt2.get());
  }

  @Test
  void hashPassword() {
    Optional<String> optionalSalt = passwordTools.generateSalt(512);
    assertTrue(optionalSalt.isPresent());

    String salt = optionalSalt.get();

    Optional<String> optionalHashedPassword = passwordTools.hashPassword(password, salt);
    Optional<String> optionalHashedPassword2 = passwordTools.hashPassword(password, salt);

    assertTrue(optionalHashedPassword.isPresent());
    assertTrue(optionalHashedPassword2.isPresent());

    assertEquals(optionalHashedPassword.get(), optionalHashedPassword2.get());
  }

  @Test
  void verify() {
    Optional<String> optionalSalt = passwordTools.generateSalt(512);
    assertTrue(optionalSalt.isPresent());

    String salt = optionalSalt.get();

    Optional<String> optionalHashedPassword = passwordTools.hashPassword(password, salt);
    assertTrue(optionalHashedPassword.isPresent());

    assertTrue(passwordTools.isValid(password, optionalHashedPassword.get(), salt));
    assertFalse(passwordTools.isValid(wrongPassword, optionalHashedPassword.get(), salt));
  }
}

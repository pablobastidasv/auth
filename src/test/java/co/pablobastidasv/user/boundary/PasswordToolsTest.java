package co.pablobastidasv.user.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class PasswordToolsTest {

  @Mock private Logger logger;
  @InjectMocks private PasswordTools passwordTools;
  private String password = "SuperSecretPassword!";
  private String wrongPassword = "ThisIsNotThePassword!";

  @BeforeEach
  void setUp() {
    passwordTools.saltLength = 16;
  }

  @Test
  void generateSalt() {
    var salt1 = passwordTools.generateSalt();
    var salt2 = passwordTools.generateSalt();

    assertNotNull(salt1);
    assertNotNull(salt2);
    assertFalse(salt1.isEmpty());
    assertFalse(salt2.isEmpty());

    assertNotEquals(salt1, salt2);
  }

  @Test
  void generateSalt_throwException(){
    passwordTools.saltLength = 0;
    assertThrows(IllegalArgumentException.class, () -> passwordTools.generateSalt());
    verify(logger).warn(anyString());
  }

  @Test
  void hashPassword() {
    var salt = passwordTools.generateSalt();
    assertNotNull(salt);

    var optionalHashedPassword = passwordTools.hashPassword(password, salt);
    var optionalHashedPassword2 = passwordTools.hashPassword(password, salt);

    assertNotNull(optionalHashedPassword);
    assertNotNull(optionalHashedPassword2);

    assertEquals(optionalHashedPassword, optionalHashedPassword2);
  }

  @Test
  void verifyGeneratedPassword() {
    var salt = passwordTools.generateSalt();
    assertNotNull(salt);

    var hashedPassword = passwordTools.hashPassword(password, salt);
    assertNotNull(hashedPassword);

    assertTrue(passwordTools.isValid(password, hashedPassword, salt));
    assertFalse(passwordTools.isValid(wrongPassword, hashedPassword, salt));
  }
}

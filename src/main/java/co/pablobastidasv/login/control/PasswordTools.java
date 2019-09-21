package co.pablobastidasv.login.control;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

@ApplicationScoped
public class PasswordTools {

  @Inject
  Logger logger;

  private static final SecureRandom RAND = new SecureRandom();

  private static final int ITERATIONS = 65536;
  private static final int KEY_LENGTH = 512;
  private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

  /**
   * Validate the password giving the encryption keys.
   *
   * @param password Plan password.
   * @param key The key to validate the password.
   * @param salt The salt used for the password encryption.
   * @return True if the password is valid, false if not.
   */
  public boolean isValid(String password, String key, String salt) {
    Optional<String> optEncrypted = hashPassword(password, salt);
    return optEncrypted.map(s -> s.equals(key)).orElse(false);
  }

  Optional<String> hashPassword(String password, String salt) {

    char[] chars = password.toCharArray();
    byte[] bytes = salt.getBytes();

    PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);

    Arrays.fill(chars, Character.MIN_VALUE);

    try {
      SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
      byte[] securePassword = fac.generateSecret(spec).getEncoded();
      return Optional.of(Base64.getEncoder().encodeToString(securePassword));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
      logger.error("Exception encountered in hashPassword()", ex);
      return Optional.empty();
    } finally {
      spec.clearPassword();
    }
  }

  Optional<String> generateSalt(final int length) {

    if (length < 1) {
      logger.warn("error in generateSalt: length must be > 0");
      return Optional.empty();
    }

    byte[] salt = new byte[length];
    RAND.nextBytes(salt);

    return Optional.of(Base64.getEncoder().encodeToString(salt));
  }
}

package co.pablobastidasv.user.boundary;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.kafka.common.errors.IllegalGenerationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

@ApplicationScoped
public class PasswordTools {

  private static final SecureRandom RAND = new SecureRandom();
  private static final int ITERATIONS = 65536;
  private static final int KEY_LENGTH = 512;
  private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

  @Inject Logger logger;

  @ConfigProperty(name = "saltLength", defaultValue = "512")
  @Inject int saltLength;

  /**
   * Validate the password giving the encryption keys.
   *
   * @param password Plan password.
   * @param key The key to validate the password.
   * @param salt The salt used for the password encryption.
   * @return True if the password is valid, false if not.
   */
  public boolean isValid(String password, String key, String salt) {
    var encrypted = hashPassword(password, salt);
    return encrypted.equals(key);
  }

  /**
   * Build a key based on the password String and a Salt.
   *
   * @param password The password in plain text
   * @param salt used to encrypt the password
   * @return A String with the password encrypted.
   */
  public String hashPassword(String password, String salt) {
    var chars = password.toCharArray();
    var bytes = salt.getBytes();

    var securePassword = hashPassword(chars, bytes);

    return Base64.getEncoder().encodeToString(securePassword);
  }

  private byte[] hashPassword(char[] chars, byte[] bytes) {
    var spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);

    Arrays.fill(chars, Character.MIN_VALUE);

    try {
      var fac = SecretKeyFactory.getInstance(ALGORITHM);
      return fac.generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
      throw new IllegalGenerationException("Exception encountered in hashPassword()");
    } finally {
      spec.clearPassword();
    }
  }

  /**
   * Generate a Salt based on the length provided via configuration.
   *
   * <p>It method use the property <code>saltLength</code> which can be set via
   * Microprofile Config specification.</p>
   *
   * @return The String salt or and empty if there is some error.
   */
  public String generateSalt() {

    if (saltLength < 1) {
      logger.warn("error in generateSalt: length must be > 0");
      throw new IllegalArgumentException("error in generateSalt: length must be > 0");
    }

    byte[] salt = new byte[saltLength];
    RAND.nextBytes(salt);

    return Base64.getEncoder().encodeToString(salt);
  }

  /**
   * Generate a random 18 characters password.
   *
   * @return A random password
   */
  public String generateRandomPassword() {
    return RAND.ints(18, 33, 122)
        .mapToObj(i -> String.valueOf((char) i))
        .collect(Collectors.joining());
  }
}

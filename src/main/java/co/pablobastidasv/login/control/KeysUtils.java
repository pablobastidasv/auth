package co.pablobastidasv.login.control;

import static co.pablobastidasv.ConfigurationConstants.JWT_KEY_ID;
import static co.pablobastidasv.ConfigurationConstants.JWT_PRIVATE_KEY;
import static co.pablobastidasv.ConfigurationConstants.MP_JWT_PUBLIC_KEY;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.keys.RsaKeyUtil;

@Dependent
public class KeysUtils {

  @ConfigProperty(name = JWT_PRIVATE_KEY)
  @Inject String privateKey;

  @ConfigProperty(name = MP_JWT_PUBLIC_KEY)
  @Inject String publicKey;

  @ConfigProperty(name = JWT_KEY_ID)
  @Inject String keyId;

  /**
   * Reading the private key String provided in property {@code co.pablobastidasv.auth.private-key}
   * generates the {@link PrivateKey} used to create the JWT.
   *
   * @return The PrivateKey ready to use.
   * @throws NoSuchAlgorithmException Error loading the algorithm.
   * @throws InvalidKeySpecException When provided key is wrong.
   */
  public PrivateKey readPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
    var decodedKey = Base64.getDecoder().decode(privateKey);
    var keySpec = new PKCS8EncodedKeySpec(decodedKey);

    return KeyFactory.getInstance(RsaKeyUtil.RSA).generatePrivate(keySpec);
  }

  /**
   * Reading the public key String provided in property {@code mp.jwt.verify.publickey} generates
   * the {@link PublicKey} used to read the JWT and create the JWK.
   *
   * @return The PublicKey ready to use.
   * @throws NoSuchAlgorithmException Error loading the algorithm.
   * @throws InvalidKeySpecException When provided key is wrong.
   */
  PublicKey readPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
    var decodedKey = Base64.getDecoder().decode(publicKey);
    var keySpec = new X509EncodedKeySpec(decodedKey);

    return KeyFactory.getInstance(RsaKeyUtil.RSA).generatePublic(keySpec);
  }

  /**
   * Reading the public and the private key build a JWS.
   *
   * @return JWS based on the public and private key.
   */
  public JWK generateJwk() {
    try {
      return new RSAKey.Builder((RSAPublicKey) readPublicKey())
          .keyUse(KeyUse.SIGNATURE)
          .keyID(keyId)
          .build();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new JwtException();
    }
  }
}

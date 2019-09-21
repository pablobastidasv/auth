package co.pablobastidasv;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.junit.jupiter.api.Test;

public class JweGenerationTest {

  @Test
  void jweGen() throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair pair = kpg.generateKeyPair();

    Key key = pair.getPublic();
    //        Key key = new AesKey(ByteUtil.randomBytes(16));
    JsonWebEncryption jwe = new JsonWebEncryption();
    jwe.setPayload("Hello World!");
    jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.RSA_OAEP_256);
    //        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
    jwe.setEncryptionMethodHeaderParameter(
        ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
    jwe.setKey(key);
    String serializedJwe = jwe.getCompactSerialization();
    System.out.println("Serialized Encrypted JWE: " + serializedJwe);

    key = pair.getPrivate();
    jwe = new JsonWebEncryption();
    jwe.setAlgorithmConstraints(
        new AlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.WHITELIST,
            KeyManagementAlgorithmIdentifiers.RSA_OAEP_256));
    jwe.setContentEncryptionAlgorithmConstraints(
        new AlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.WHITELIST,
            ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256));
    jwe.setKey(key);
    jwe.setCompactSerialization(serializedJwe);

    System.out.println("Payload: " + jwe.getPayload());
  }
}

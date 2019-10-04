package co.pablobastidasv;

import java.security.KeyPairGenerator;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.junit.jupiter.api.Test;

public class JweGenerationTest {

  @Test
  void jweGen() throws Exception {
    var kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    var pair = kpg.generateKeyPair();

    var publicKey = pair.getPublic();
    //        Key key = new AesKey(ByteUtil.randomBytes(16));
    var jwe = new JsonWebEncryption();
    jwe.setPayload("Hello World!");
    jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.RSA_OAEP_256);
    //        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
    jwe.setEncryptionMethodHeaderParameter(
        ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
    jwe.setKey(publicKey);
    var serializedJwe = jwe.getCompactSerialization();
    System.out.println("Serialized Encrypted JWE: " + serializedJwe);

    var privateKey = pair.getPrivate();
    jwe = new JsonWebEncryption();
    jwe.setAlgorithmConstraints(
        new AlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.WHITELIST,
            KeyManagementAlgorithmIdentifiers.RSA_OAEP_256));
    jwe.setContentEncryptionAlgorithmConstraints(
        new AlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.WHITELIST,
            ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256));
    jwe.setKey(privateKey);
    jwe.setCompactSerialization(serializedJwe);

    System.out.println("Payload: " + jwe.getPayload());
  }
}

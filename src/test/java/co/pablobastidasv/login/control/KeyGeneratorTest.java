package co.pablobastidasv.login.control;

import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import org.junit.jupiter.api.Test;

class KeyGeneratorTest {

  private static byte[] makeWritable(Key key) {
    var encoded = key.getEncoded();
    return makeWritable(encoded);
  }

  private static byte[] makeWritable(byte[] content) {
    return Base64.getEncoder().encode(content);
  }

  @Test
  void generateKeys() throws Exception {
    var kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    var pair = kpg.generateKeyPair();

    var privateKey = pair.getPrivate();
    var publicKey = pair.getPublic();
    var encoded = new PKCS8EncodedKeySpec(publicKey.getEncoded());
    var privateKeyString = makeWritable(privateKey);
    var publicKeyString = makeWritable(encoded.getEncoded());
    System.out.println("private key---");
    System.out.write(privateKeyString, 0, privateKeyString.length);
    System.out.println("\n---");
    System.out.println("public key---");
    System.out.write(publicKeyString, 0, publicKeyString.length);
    System.out.println("\n---");

    //        generateKeyStore(publicKey);
  }

  private void generateKeyStore(PublicKey key) throws Exception {
    var keyStore = KeyStore.getInstance("JCEKS");
    keyStore.load(null, null);

    keyStore.setKeyEntry("secret", key, "password".toCharArray(), null);

    keyStore.store(new FileOutputStream("output.jceks"), "password".toCharArray());
  }
}

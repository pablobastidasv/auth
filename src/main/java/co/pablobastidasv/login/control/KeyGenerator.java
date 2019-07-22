package co.pablobastidasv.login.control;

import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class KeyGenerator {
    public static KeyPair generateKeys() throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair pair = kpg.generateKeyPair();

        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        PKCS8EncodedKeySpec encoded = new PKCS8EncodedKeySpec(publicKey.getEncoded());
        byte[] privateKeyString = makeWritable(privateKey);
        byte[] publicKeyString = makeWritable(encoded.getEncoded());
        System.out.println("private key---");
        System.out.write(privateKeyString, 0, privateKeyString.length);
        System.out.println("\n---");
        System.out.println("public key---");
        System.out.write(publicKeyString, 0, publicKeyString.length);
        System.out.println("\n---");

        return pair;
    }

    private static byte[] makeWritable(Key key) {
        byte[] encoded = key.getEncoded();
        return makeWritable(encoded);
    }

    private static byte[] makeWritable(byte[] content) {
        return Base64.getEncoder().encode(content);
    }
}

package co.pablobastidasv.login.control;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.keys.RsaKeyUtil;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static co.pablobastidasv.ConfigurationConstants.JWT_PRIVATE_KEY;

@Dependent
public class PrivateKeyUtils {

    @Inject
    @ConfigProperty(name = JWT_PRIVATE_KEY)
    String privateKey;

    public PrivateKey readPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedKey = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

        return KeyFactory.getInstance(RsaKeyUtil.RSA)
            .generatePrivate(keySpec);
    }
}

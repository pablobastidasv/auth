package co.pablobastidasv.login.control;

import static co.pablobastidasv.login.TestConstants.KEY_ID;
import static co.pablobastidasv.login.TestConstants.PRIVATE_KEY;
import static co.pablobastidasv.login.TestConstants.PUBLIC_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nimbusds.jose.jwk.JWK;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KeysUtilsTest {

  KeysUtils keysUtils;

  @BeforeEach
  void init() {
    keysUtils = new KeysUtils();

    keysUtils.privateKey = PRIVATE_KEY;
    keysUtils.publicKey = PUBLIC_KEY;
    keysUtils.keyId = KEY_ID;
  }

  @Test
  void generateJwk() {
    var jwk = keysUtils.generateJwk();

    assertNotNull(jwk);
    assertEquals(KEY_ID, jwk.getKeyID());
  }
}

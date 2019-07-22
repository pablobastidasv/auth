package co.pablobastidasv.login.control;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class KeyGeneratorTest {

    @Test
    void generateKeys() throws IOException, NoSuchAlgorithmException {
        KeyGenerator.generateKeys();
    }
}
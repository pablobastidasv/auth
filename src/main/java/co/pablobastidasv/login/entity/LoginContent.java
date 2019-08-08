package co.pablobastidasv.login.entity;

import com.nimbusds.jwt.SignedJWT;

public class LoginContent {
    public String accessToken;
    public Long expiresIn;

    public LoginContent() {
    }

    public LoginContent(SignedJWT signedJWT, long expiresIn) {
        this.accessToken = signedJWT.serialize();
        this.expiresIn = expiresIn;
    }

}

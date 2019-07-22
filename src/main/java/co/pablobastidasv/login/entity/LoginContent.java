package co.pablobastidasv.login.entity;

import com.nimbusds.jwt.SignedJWT;

public class LoginContent {
    private SignedJWT signedJWT;
    private long expiresIn;

    public LoginContent() {
    }

    public LoginContent(SignedJWT signedJWT, long expiresIn) {
        this.signedJWT = signedJWT;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return signedJWT.serialize();
    }

    public long getExpiresIn() {
        return expiresIn;
    }

}

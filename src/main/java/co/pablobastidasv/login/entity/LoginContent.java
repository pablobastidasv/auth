package co.pablobastidasv.login.entity;

import com.nimbusds.jwt.SignedJWT;

public class LoginContent {
  public String accessToken;
  public Long expiresIn;
  public boolean changePassword;

  public LoginContent() {
    this.changePassword = true;
  }

  public LoginContent(SignedJWT signedJwt, long expiresIn) {
    this.accessToken = signedJwt.serialize();
    this.expiresIn = expiresIn;
  }
}

package co.pablobastidasv.login.boundary;

import co.pablobastidasv.login.control.JwtException;
import co.pablobastidasv.login.control.JwtUtils;
import co.pablobastidasv.login.control.KeysUtils;
import co.pablobastidasv.user.entity.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/** Utilities for generating a JWT for testing. */
@ApplicationScoped
public class TokenGenerator {

  @Inject KeysUtils keysUtils;

  @Inject JwtUtils jwtUtils;

  /**
   * Utility method to generate a SignedJWT from a Map of claims.
   *
   * @param claims - Claims information.
   * @return the JWT Signed.
   */
  public SignedJWT generateSignedToken(Map<String, Object> claims) {
    JWTClaimsSet.Builder claimsBuilder = jwtUtils.generateClaims(claims);
    jwtUtils.mandatoryClaims(claimsBuilder);

    return generateSignedToken(claimsBuilder.build());
  }

  /**
   * Utility method to generate a SignedJWT from a User and tenant.
   *
   * @param user - JWT claims will be based on the information from the user.
   * @param tenant - Tenant ID where the user belongs.
   * @return the JWT Signed.
   */
  public SignedJWT generateSignedToken(User user, String tenant) {
    JWTClaimsSet.Builder claimsBuilder = jwtUtils.generateClaims(user);
    jwtUtils.mandatoryClaims(claimsBuilder);
    claimsBuilder.claim("tenant", tenant);

    return generateSignedToken(claimsBuilder.build());
  }

  private SignedJWT generateSignedToken(JWTClaimsSet claimsSet) {
    try {
      PrivateKey pk = keysUtils.readPrivateKey();
      JWSSigner signer = new RSASSASigner(pk);
      JWSHeader jwsHeader = jwtUtils.fillJwsHeader();

      SignedJWT signedJwt = new SignedJWT(jwsHeader, claimsSet);
      signedJwt.sign(signer);

      return signedJwt;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {
      throw new JwtException();
    }
  }
}

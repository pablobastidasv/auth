package co.pablobastidasv.login.boundary;

import co.pablobastidasv.login.control.JwtException;
import co.pablobastidasv.login.control.JwtFiller;
import co.pablobastidasv.login.control.PrivateKeyUtils;
import co.pablobastidasv.user.entity.Role;
import co.pablobastidasv.user.entity.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utilities for generating a JWT for testing
 */
@ApplicationScoped
public class TokenGenerator {

    @Inject
    PrivateKeyUtils privateKeyUtils;

    @Inject
    JwtFiller jwtFiller;

    /**
     * Utility method to generate a JWT string from a String.
     *
     * @param user  - JWT claims will be based on the information from the user.
     * @param tenant - Tenant ID where the user belongs.
     * @return the JWT Signed.
     */
    public SignedJWT generateSignedToken(User user, String tenant) {
        try {
            JWTClaimsSet.Builder claimsBuilder = jwtFiller.generateUserClaim(user);
            jwtFiller.defineTimeClaims(claimsBuilder);
            claimsBuilder.claim("tenant", tenant);

            PrivateKey pk = privateKeyUtils.readPrivateKey();
            JWSSigner signer = new RSASSASigner(pk);
            JWSHeader jwsHeader = jwtFiller.fillJwsHeader();

            JWTClaimsSet claimsSet = claimsBuilder.build();
//            for (String claim : claimsSet.getClaims().keySet()) {
//                Object claimValue = claimsSet.getClaim(claim);
//                System.out.printf("\tAdded claim: %s, value: %s%n", claim, claimValue);
//            }

            SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
            signedJWT.sign(signer);
            return signedJWT;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {
            throw new JwtException();
        }
    }

}
package co.pablobastidasv.login.control;

import co.pablobastidasv.user.entity.Role;
import co.pablobastidasv.user.entity.User;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

import static co.pablobastidasv.ConfigurationConstants.*;

@ApplicationScoped
public class JwtUtils {

    private static final String DEFAULT_ISSUER = "localhost";

    public static final String USER_ID_CLAIM = "user_id";

    @Inject
    Clock clock;

    @Inject
    @ConfigProperty(name = JWT_KEY_ID)
    String keyId;

    @Inject
    @ConfigProperty(name = JWT_EXPIRES_IN, defaultValue = JWT_EXPIRES_IN_DEFAULT)
    Integer expiresIn;

    public JWTClaimsSet.Builder generateClaims(Map<String, Object> claimsMap) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

        for (String s : claimsMap.keySet()) {
            builder.claim(s, claimsMap.get(s));
        }

        return builder;
    }

    public JWTClaimsSet.Builder generateClaims(User user) {
        String iss = defineIssuer().orElse(DEFAULT_ISSUER);
        Set<String> group = user.getRoles().stream().map(Role::getId).collect(Collectors.toSet());

        return new JWTClaimsSet.Builder()
            .claim(USER_ID_CLAIM, user.getId())
            .claim(Claims.jti.name(), UUID.randomUUID().toString())
            .claim(Claims.upn.name(), user.getUsername())
            .claim(Claims.sub.name(), user.getUsername())
            .claim(Claims.iss.name(), iss)
            .claim(Claims.groups.name(), group);
    }

    Optional<String> defineIssuer(){
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            System.out.println("Your current IP address : " + ip);
            System.out.println("Your current Hostname (iss) : " + hostname);

            return Optional.of(hostname);
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }

    public void defineTimeClaims(JWTClaimsSet.Builder claimsBuilder) {
        long currentTimeInSecs = currentTimeInSecs();
        long exp = currentTimeInSecs + expiresIn;

        System.out.printf("Setting exp: %d / %s%n", exp, new Date(1000 * exp));

        claimsBuilder.claim(Claims.exp.name(), exp);
        claimsBuilder.claim(Claims.iat.name(), currentTimeInSecs);
        claimsBuilder.claim(Claims.auth_time.name(), currentTimeInSecs);
    }

    public JWSHeader fillJwsHeader(){
        return new JWSHeader.Builder(JWSAlgorithm.RS256)
            .keyID(keyId)
            .type(JOSEObjectType.JWT)
            .build();
    }

    /**
     * @return the current time in seconds since epoch
     */
    long currentTimeInSecs() {
        return clock.millis() / 1_000;
    }

    public Map<String, Object> mapFromClaims(JsonWebToken jsonWebToken) {
        Map<String, Object> claims = new HashMap<>();

        for (String claimName : jsonWebToken.getClaimNames()) {
            Object claimValue = jsonWebToken.getClaim(claimName);
            claims.put(claimName, claimValue);
        }

        return claims;
    }
}

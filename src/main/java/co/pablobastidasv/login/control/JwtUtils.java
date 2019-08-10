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
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.time.Clock;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static co.pablobastidasv.ConfigurationConstants.*;

@ApplicationScoped
public class JwtUtils {

    public static final String USER_ID_CLAIM = "user_id";

    @Inject
    Clock clock;

    @Inject
    @ConfigProperty(name = JWT_KEY_ID)
    String keyId;
    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "http://localhost")
    String issuer;

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
        Set<String> group = user.getRoles().stream().map(Role::getId).collect(Collectors.toSet());

        return new JWTClaimsSet.Builder()
            .claim(USER_ID_CLAIM, user.getId())
            .claim(Claims.upn.name(), user.getUsername())
            .claim(Claims.sub.name(), user.getUsername())
            .claim(Claims.groups.name(), group);
    }

    public void mandatoryClaims(JWTClaimsSet.Builder claimsBuilder) {
        long currentTimeInSecs = currentTimeInSecs();
        long exp = currentTimeInSecs + expiresIn;

        claimsBuilder.claim(Claims.iss.name(), issuer)
            .claim(Claims.jti.name(), UUID.randomUUID().toString())
            .claim(Claims.exp.name(), exp)
            .claim(Claims.iat.name(), currentTimeInSecs)
            .claim(Claims.auth_time.name(), currentTimeInSecs);
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
        return jsonWebToken.getClaimNames().stream()
            .filter(this::isCopyable)
            .collect(Collectors.toMap(
                claimName -> claimName,
                claimName -> extractClaimValue(jsonWebToken, claimName)
            ));
    }

    private Object extractClaimValue(JsonWebToken jsonWebToken, String claimName) {
        Object claimValue = jsonWebToken.getClaim(claimName);

        if(claimValue instanceof JsonValue){
            JsonValue value = (JsonValue) claimValue;
            switch (value.getValueType()) {
                case NULL: return "null";
                case NUMBER: return ((JsonNumber) value).bigIntegerValue();
                case STRING: return ((JsonString) value).getString();
                case TRUE: return true;
                case FALSE: return false;
                default: return "";
            }
        } else {
            return claimValue;
        }
    }

    private boolean isCopyable(String claimName) {
        return !Claims.raw_token.name().equalsIgnoreCase(claimName);
    }
}

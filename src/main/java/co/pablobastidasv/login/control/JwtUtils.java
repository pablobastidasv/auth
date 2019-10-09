package co.pablobastidasv.login.control;

import static co.pablobastidasv.ConfigurationConstants.JWT_AUD;
import static co.pablobastidasv.ConfigurationConstants.JWT_EXPIRES_IN;
import static co.pablobastidasv.ConfigurationConstants.JWT_EXPIRES_IN_DEFAULT;
import static co.pablobastidasv.ConfigurationConstants.JWT_KEY_ID;

import co.pablobastidasv.user.entity.Role;
import co.pablobastidasv.user.entity.SystemUser;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import java.time.Clock;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class JwtUtils {

  public static final String USER_ID_CLAIM = "user_id";

  @Inject Clock clock;

  @ConfigProperty(name = JWT_KEY_ID)
  @Inject String keyId;

  @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "http://localhost")
  @Inject String issuer;

  @ConfigProperty(name = JWT_AUD, defaultValue = "avalane")
  @Inject String aud;

  @ConfigProperty(name = JWT_EXPIRES_IN, defaultValue = JWT_EXPIRES_IN_DEFAULT)
  @Inject Integer expiresIn;

  /**
   * Based on a key value object representing claim values, generate a {@link JWTClaimsSet.Builder}
   * with all this info.
   *
   * @param claimsMap A key-value object with the info that will be mapped to claims.
   * @return A builder ready to work with the information of the input.
   */
  public JWTClaimsSet.Builder generateClaims(Map<String, Object> claimsMap) {
    var builder = new JWTClaimsSet.Builder();

    for (String s : claimsMap.keySet()) {
      builder.claim(s, claimsMap.get(s));
    }

    return builder;
  }

  /**
   * Based on the user information a {@link JWTClaimsSet.Builder} is created.
   *
   * <p>The Builder will contain below claims:</p>
   *
   * <ul>
   *   <li><strong>upn</strong>: with the <i>username</i> information</li>
   *   <li><strong>sub</strong>: with the <i>username</i> information</li>
   *   <li><strong>groups</strong>: with the <i>roles</i> information</li>
   *   <li><strong>user_id</strong>: with the <i>userId</i> information</li>
   * </ul>
   *
   * @param systemUser The user from the database.
   * @return A {@link JWTClaimsSet.Builder} with the user information.
   */
  public JWTClaimsSet.Builder generateClaims(SystemUser systemUser) {
    Set<String> group = systemUser.getRoles().stream().map(Role::getId).collect(Collectors.toSet());

    return new JWTClaimsSet.Builder()
        .claim(USER_ID_CLAIM, systemUser.getId())
        .claim(Claims.upn.name(), systemUser.getUsername())
        .claim(Claims.sub.name(), systemUser.getUsername())
        .claim(Claims.groups.name(), group);
  }

  /**
   * Fill claim provided {@link JWTClaimsSet.Builder} object provided with JWT mandatory claims.
   *
   * @param claimsBuilder The builder where the claims will be added.
   */
  public void mandatoryClaims(JWTClaimsSet.Builder claimsBuilder) {
    var currentTimeInSecs = currentTimeInSecs();
    var exp = currentTimeInSecs + expiresIn;

    claimsBuilder.claim(Claims.iss.name(), issuer)
        .claim(Claims.jti.name(), UUID.randomUUID().toString())
        .claim(Claims.exp.name(), exp)
        .claim(Claims.aud.name(), aud)
        .claim(Claims.iat.name(), currentTimeInSecs)
        .claim(Claims.auth_time.name(), currentTimeInSecs);
  }

  /**
   * Generates the JWSHeader object using the {@code co.pablobastidasv.auth.jwt.key-id} property
   * value that will can be used to generate the JWT.
   *
   * @return A JwsHeader ready to use.
   */
  public JWSHeader fillJwsHeader() {
    return new JWSHeader.Builder(JWSAlgorithm.RS256)
        .keyID(keyId)
        .type(JOSEObjectType.JWT)
        .build();
  }

  /**
   * Method to obtain the current UTC time in seconds.
   *
   * @return the current time in seconds since epoch.
   */
  private long currentTimeInSecs() {
    return clock.millis() / 1_000;
  }

  /**
   * Extract the claims information from a {@link JsonWebToken} and stored in a Map to
   * work with it.
   *
   * @param jsonWebToken The JWT object from the logged user.
   * @return A map with all the claims from the JWT.
   */
  public Map<String, Object> mapFromClaims(JsonWebToken jsonWebToken) {
    return jsonWebToken.getClaimNames().stream()
        .filter(this::isCopyable)
        .collect(Collectors.toMap(
            claimName -> claimName,
            claimName -> extractClaimValue(jsonWebToken, claimName)
        ));
  }

  private Object extractClaimValue(JsonWebToken jsonWebToken, String claimName) {
    var claimValue = jsonWebToken.getClaim(claimName);

    if (claimValue instanceof JsonValue) {
      var value = (JsonValue) claimValue;
      switch (value.getValueType()) {
        case NULL:
          return "null";
        case NUMBER:
          return ((JsonNumber) value).bigIntegerValue();
        case STRING:
          return ((JsonString) value).getString();
        case TRUE:
          return true;
        case FALSE:
          return false;
        default:
          return "";
      }
    } else {
      return claimValue;
    }
  }

  private boolean isCopyable(String claimName) {
    return !Claims.raw_token.name().equalsIgnoreCase(claimName);
  }
}

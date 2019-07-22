package co.pablobastidasv.login.control;

import co.pablobastidasv.user.entity.Role;
import co.pablobastidasv.user.entity.User;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import org.eclipse.microprofile.jwt.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.time.Clock;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.wildfly.common.Assert.assertNotNull;

@ExtendWith(MockitoExtension.class)
class JwtFillerTest {

    private static final String expectedUsername = "username";
    private static final long currentTime = 1563339415000L;
    private static final long expectedId = new Random().nextLong();
    private static final List<Role> roles = new ArrayList<>();

    @Mock
    private Clock clock;

    @InjectMocks
    private JwtFiller jwtFiller;

    private Integer expiresIn = 300;

    @BeforeEach
    void init() {
        jwtFiller.keyId = "JWTSuperSecureKeyNoSharePlease!!!";
        jwtFiller.expiresIn = expiresIn;
    }

    @Test
    @SuppressWarnings("unchecked")
    void fillClaim() throws Exception {
        // Given
        roles.add(new Role("ADMIN", "Administrator"));

        User user = new User();
        user.setId(expectedId);
        user.setUsername(expectedUsername);
        user.setRoles(roles);

        // When
        JWTClaimsSet.Builder builder = jwtFiller.generateUserClaim(user);
        JWTClaimsSet claimsSet = builder.build();

        // Then
        testStringClaim(claimsSet, expectedUsername, Claims.sub.name());
        testLongClaim(claimsSet, expectedId, Claims.upn.name());
        assertNotNull(claimsSet.getIssuer());

        Set<String> groupsFromClaim = (Set<String>) claimsSet.getClaim(Claims.groups.name());
        assertNotNull(groupsFromClaim);
        assertEquals(roles.size(), groupsFromClaim.size());
    }

    @Test
    void defineIssuer(){
        Optional<String> issuer = jwtFiller.defineIssuer();

        assertTrue(issuer.isPresent());
    }

    @Test
    void defineTimeClaims() throws Exception {
        // Given
        when(clock.millis()).thenReturn(currentTime);
        Long expectedCurrentTime = currentTime / 1000;
        Long expectedExpTime = expectedCurrentTime + expiresIn;

        // When
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        jwtFiller.defineTimeClaims(builder);
        JWTClaimsSet claimsSet = builder.build();

        // Then
        testLongClaim(claimsSet, expectedExpTime, Claims.exp.name());
        testLongClaim(claimsSet, expectedCurrentTime, Claims.iat.name());
        testLongClaim(claimsSet, expectedCurrentTime, Claims.auth_time.name());

    }

    @Test
    void fillJwsHeader() {
        JWSHeader header = jwtFiller.fillJwsHeader();

        assertEquals(JWSAlgorithm.RS256, header.getAlgorithm());
        assertEquals(jwtFiller.keyId, header.getKeyID());
        assertEquals(JOSEObjectType.JWT, header.getType());
    }

    private void testLongClaim(JWTClaimsSet claimsSet, Long expected, String claimName) throws ParseException {
        Long claimValue = claimsSet.getLongClaim(claimName);

        assertNotNull(claimValue);
        assertEquals(expected, claimValue);
    }
    private void testStringClaim(JWTClaimsSet claimsSet, String expected, String claimName) throws ParseException {
        String claimValue = claimsSet.getStringClaim(claimName);

        assertNotNull(claimValue);
        assertEquals(expected, claimValue);
    }
}
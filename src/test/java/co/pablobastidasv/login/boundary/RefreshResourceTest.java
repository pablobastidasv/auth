package co.pablobastidasv.login.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.pablobastidasv.login.control.JwtUtils;
import co.pablobastidasv.login.entity.LoginContent;
import com.nimbusds.jwt.SignedJWT;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshResourceTest {

  @Mock JwtUtils jwtUtils;
  @Mock JsonWebToken jsonWebToken;

  @Mock TokenGenerator tokenGenerator;

  @InjectMocks RefreshResource refreshResource;

  @Test
  void refresh_Unauthorized() {
    // Given
    SecurityContext ctx = mock(SecurityContext.class);

    // When
    Response response = refreshResource.refresh(ctx);

    // Then
    assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
  }

  @Test
  void refresh() {
    // Given
    SecurityContext ctx = mock(SecurityContext.class);
    Principal principal = mock(Principal.class);
    when(ctx.getUserPrincipal()).thenReturn(principal);

    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", "username");
    claims.put("email", "username@test.com");
    claims.put("ageOfBirth", new Date());
    when(jwtUtils.mapFromClaims(jsonWebToken)).thenReturn(claims);

    String myJwtToken = "my_jwt_token";
    SignedJWT signedJWT = mock(SignedJWT.class);
    when(signedJWT.serialize()).thenReturn(myJwtToken);
    when(tokenGenerator.generateSignedToken(claims)).thenReturn(signedJWT);

    refreshResource.expiresIn = 300;

    // When
    Response response = refreshResource.refresh(ctx);

    // Then
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertEquals(LoginContent.class, response.getEntity().getClass());

    LoginContent content = (LoginContent) response.getEntity();

    assertNotNull(content.accessToken);
    assertEquals(myJwtToken, content.accessToken);
    assertTrue(content.expiresIn > 0);
  }
}

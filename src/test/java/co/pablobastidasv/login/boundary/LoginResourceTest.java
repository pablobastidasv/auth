package co.pablobastidasv.login.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.pablobastidasv.user.boundary.PasswordTools;
import co.pablobastidasv.login.entity.LoginContent;
import co.pablobastidasv.user.boundary.UserManager;
import co.pablobastidasv.user.entity.State;
import co.pablobastidasv.user.entity.SystemUser;
import com.nimbusds.jwt.SignedJWT;
import java.util.Optional;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginResourceTest {

  private static final String username = "username";
  private static final String tenant = "avalane";
  @Mock TokenGenerator tokenGenerator;
  @Mock private PasswordTools passwordTools;
  @Mock private UserManager userManager;
  @InjectMocks private LoginResource resource;

  @BeforeEach
  void init() {
    resource.expiresIn = 100;
  }

  @Test
  void login_usernameNotFound() {
    when(userManager.findByUserAndTenant(username, tenant)).thenReturn(Optional.empty());
    resource.tenantId = tenant;

    Response login = resource.login(username, "password");

    assertNotNull(login);
    assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), login.getStatus());
  }

  @Test
  void login_changePassword() {
    SystemUser systemUser = new SystemUser();
    systemUser.setState(State.CHANGE_PWD);

    when(userManager.findByUserAndTenant(username, tenant)).thenReturn(Optional.of(systemUser));
    resource.tenantId = tenant;

    Response login = resource.login(username, "password");

    assertNotNull(login);
    assertEquals(Response.Status.OK.getStatusCode(), login.getStatus());

    LoginContent loginContent = (LoginContent) login.getEntity();
    assertTrue(loginContent.changePassword);
  }

  @Test
  void login_changeJustCreated() {
    SystemUser systemUser = new SystemUser();
    systemUser.setState(State.CREATED);

    when(userManager.findByUserAndTenant(username, tenant)).thenReturn(Optional.of(systemUser));
    resource.tenantId = tenant;

    Response login = resource.login(username, "password");

    assertNotNull(login);
    assertEquals(Response.Status.OK.getStatusCode(), login.getStatus());

    LoginContent loginContent = (LoginContent) login.getEntity();
    assertTrue(loginContent.changePassword);
  }

  @Test
  void login_userActive() {
    SystemUser systemUser = new SystemUser();
    systemUser.setState(State.ACTIVE);

    when(tokenGenerator.generateSignedToken(any(), anyString())).thenReturn(mock(SignedJWT.class));
    when(passwordTools.isValid(anyString(), any(), any())).thenReturn(true);
    when(userManager.findByUserAndTenant(username, tenant)).thenReturn(Optional.of(systemUser));
    resource.tenantId = tenant;

    Response login = resource.login(username, "password");

    assertNotNull(login);
    assertEquals(Response.Status.OK.getStatusCode(), login.getStatus());

    LoginContent loginContent = (LoginContent) login.getEntity();
    assertFalse(loginContent.changePassword);
  }
}

package co.pablobastidasv.login.boundary;

import co.pablobastidasv.login.control.PasswordTools;
import co.pablobastidasv.user.boundary.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginResourceTest {

    @Mock
    private PasswordTools passwordTools;

    @Mock
    private UserManager userManager;

    @InjectMocks
    private LoginResource resource;

    private static final String username = "username";
    private static final String tenant = "avalane";

    @BeforeEach
    void init(){
        resource.expiresIn = 100;
    }

    @Test
    void login_usernameNotFound() throws Exception{
        when(userManager.findByUserAndTenant(username, tenant)).thenReturn(Optional.empty());
        resource.tenantId = tenant;

        Response login = resource.login(username, "password");

        assertNotNull(login);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), login.getStatus());
    }
}
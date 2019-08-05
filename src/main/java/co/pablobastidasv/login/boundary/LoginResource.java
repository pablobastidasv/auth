package co.pablobastidasv.login.boundary;

import co.pablobastidasv.login.control.PasswordTools;
import co.pablobastidasv.login.entity.LoginContent;
import co.pablobastidasv.user.boundary.UserManager;
import co.pablobastidasv.user.entity.User;
import com.nimbusds.jwt.SignedJWT;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static co.pablobastidasv.ConfigurationConstants.JWT_EXPIRES_IN;
import static co.pablobastidasv.ConfigurationConstants.JWT_EXPIRES_IN_DEFAULT;

@Path("/{tenantId}/login")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    @PathParam("tenantId")
    String tenantId;

    @Inject
    @ConfigProperty(name = JWT_EXPIRES_IN, defaultValue = JWT_EXPIRES_IN_DEFAULT)
    Integer expiresIn;

    @Inject
    PasswordTools passwordTools;

    @Inject
    UserManager userManager;

    @Inject
    TokenGenerator tokenGenerator;

    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {
        Optional<User> userOpt = userManager.findByUserAndTenant(username, tenantId);

        if(userOpt.isPresent()){
            User user = userOpt.get();

            if (!passwordTools.validatePassword(password, user.getKey(), user.getSalt())){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            SignedJWT jwt = tokenGenerator.generateSignedToken(user, tenantId);

            LoginContent content = new LoginContent(jwt, expiresIn);

            return Response.ok(content).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }
}

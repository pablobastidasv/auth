package co.pablobastidasv.login.boundary;

import static co.pablobastidasv.ConfigurationConstants.JWT_EXPIRES_IN;
import static co.pablobastidasv.ConfigurationConstants.JWT_EXPIRES_IN_DEFAULT;

import co.pablobastidasv.login.control.PasswordTools;
import co.pablobastidasv.login.entity.LoginContent;
import co.pablobastidasv.user.boundary.UserManager;
import co.pablobastidasv.user.entity.User;
import com.nimbusds.jwt.SignedJWT;
import java.util.Optional;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

@Path("/{tenantId}/login")
@RequestScoped
public class LoginResource {

  @Inject
  Logger logger;

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

  /**
   * Rest endpoint to authenticate a user based on their username and password.
   *
   * <p>If the authentication is correct, the response will have a status 200 and
   * the entity in the response will contain the JWT that can be used to authenticate the
   * user in other services.</p>
   *
   * <p>If user does not exist, the response will have a status 401</p>
   *
   * @param username User name to login
   * @param password Password to login
   * @return When {@link Response.Status} is OK, a {@link LoginContent} with the information
   *         about the JWT and more information.
   */
  @POST
  @PermitAll
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response login(
      @FormParam("username") String username,
      @FormParam("password") String password
  ) {
    Optional<User> userOpt = userManager.findByUserAndTenant(username, tenantId);

    if (userOpt.isPresent()) {
      User user = userOpt.get();
      return defineResponse(user, password);
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  private Response defineResponse(User user, String password) {
    switch (user.getState()){
      case BLOCKED:
        return Response.status(Response.Status.UNAUTHORIZED).build();
      case CREATED:
      case CHANGE_PWD:
        logger.warn("Response for redirection");
      default:
    }

    if(isPasswordValid(user, password)){
      return buildOkResponse(user);
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  private boolean isPasswordValid(User user, String password) {
    return passwordTools.isValid(password, user.getKey(), user.getSalt());
  }

  private Response buildOkResponse(User user) {
    SignedJWT jwt = tokenGenerator.generateSignedToken(user, tenantId);
    LoginContent content = new LoginContent(jwt, expiresIn);
    return Response.ok(content).build();
  }
}

package co.pablobastidasv.user.boundary;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

@RequestScoped
@Path("/users")
public class UserResource {

  @Inject
  UserManager userManager;


}

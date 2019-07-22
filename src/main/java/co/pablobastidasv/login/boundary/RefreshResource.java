package co.pablobastidasv.login.boundary;

import co.pablobastidasv.login.entity.LoginContent;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Objects;

@Path("/refresh")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class RefreshResource {

    @Inject
    JsonWebToken jsonWebToken;

    @GET
    @Consumes(MediaType.MEDIA_TYPE_WILDCARD)
    public Response refresh(@Context SecurityContext ctx) throws Exception {
        if(Objects.isNull(ctx.getUserPrincipal())) return Response.status(401).build();

//        String jwt = LoginResource.generateToken();
//        LoginContent content = new LoginContent(jwt, 300);
//        return Response.ok(content).build();
        return Response.ok().build();
    }
}

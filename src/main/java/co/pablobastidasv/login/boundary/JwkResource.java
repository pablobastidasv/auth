package co.pablobastidasv.login.boundary;

import co.pablobastidasv.login.control.KeysUtils;
import com.nimbusds.jose.jwk.JWK;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.minidev.json.JSONObject;

@Path("/jwk")
@Produces(MediaType.APPLICATION_JSON)
public class JwkResource {

  @Inject KeysUtils keysUtils;

  /**
   * Rest endpoint to provide the JsonWebKey.
   *
   * @return Json response with content object {@link JSONObject}
   */
  @GET
  public Response retrieveJwk() {
    var jwk = keysUtils.generateJwk();

    return Response.ok(jwk.toJSONObject()).build();
  }
}

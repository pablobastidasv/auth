package co.pablobastidasv.login.control;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class JwtException extends WebApplicationException {
  public JwtException() {
    super(Response.Status.INTERNAL_SERVER_ERROR);
  }
}

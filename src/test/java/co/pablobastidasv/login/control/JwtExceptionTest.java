package co.pablobastidasv.login.control;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JwtExceptionTest {

  @Test
  void internalErrorResponse() {
    var jwtException = new JwtException();

    assertEquals(500, jwtException.getResponse().getStatus());
  }
}

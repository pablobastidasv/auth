package co.pablobastidasv.login.control;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JwtExceptionTest {

  @Test
  void internalErrorResponse(){
    JwtException jwtException = new JwtException();

    assertEquals(500, jwtException.getResponse().getStatus());
  }

}
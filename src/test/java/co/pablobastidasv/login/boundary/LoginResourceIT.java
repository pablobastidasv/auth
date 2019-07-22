package co.pablobastidasv.login.boundary;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
class LoginResourceIT {

    private static final String username = "username";
    private static final String usernameNoRol = "user";
    private static final String password = "SuperSecretPassword!";
    private static final String tenant = "avalane";

    @Test
    void login_unauthorized() {
        given()
            .when()
            .contentType(ContentType.URLENC)
            .param("username", "pepito")
            .param("password", "password")
            .post(tenant + "/login")
            .then()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        given()
            .when()
            .contentType(ContentType.URLENC)
            .param("username", username)
            .param("password", "Hola")
            .post(tenant + "/login")
            .then()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    void login() {
        String accessToken = given()
            .when()
            .contentType(ContentType.URLENC)
            .param("username", username)
            .param("password", password)
            .post(tenant + "/login")
            .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("accessToken", notNullValue())
            .body("expiresIn", equalTo(300))
            .extract()
            .path("accessToken").toString();
    }

    @Test
    void login_userNoRole() {
        given()
            .when()
            .contentType(ContentType.URLENC)
            .param("username", usernameNoRol)
            .param("password", password)
            .post(tenant + "/login")
            .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("accessToken", notNullValue())
            .body("expiresIn", equalTo(300)) // Default expires in value
        ;
    }
}
package co.pablobastidasv.login.boundary;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
class AuthenticationResourcesIT {

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
    void login(){
        getAccessToken();
    }

    @Test
    void refresh() {
        String accessToken = getAccessToken();

        given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get("/refresh")
            .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("accessToken", notNullValue())
            .body("expiresIn", equalTo(300))// Default expires in value
        ;
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

    private String getAccessToken() {
        return given()
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
}
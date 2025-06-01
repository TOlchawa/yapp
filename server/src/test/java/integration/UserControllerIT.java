package integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("integration")
public class UserControllerIT {

    @EnabledIf(expression = "#{systemEnvironment['PROD'] == null}", reason = "Disabled in PROD environment")
    @Test
    void testCreateUser() {
        RestAssured.baseURI = "http://localhost:9090";
//        RestAssured.baseURI = "https://memoritta.com/api";

        String emailAddress = "newuser" + UUID.randomUUID() + "@example.com";

        Response response = given()
                .auth().basic("admin", "admin")
                .contentType("application/x-www-form-urlencoded")
                .param("email", emailAddress)
                .param("password", "securePassword123")
                .param("nickname", "NewUser")
                .when()
                .put("/user")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        // Extract and validate the UUID of the created user
        UUID createdUserId = response.as(UUID.class);
        assertThat(createdUserId).isNotNull();

        Response response2 = given()
                .auth().basic("admin", "admin")
                .contentType("application/x-www-form-urlencoded")
                .param("email", emailAddress)
                .param("password", "securePassword123")
                .param("nickname", "NewUser")
                .when()
                .put("/user")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .response();

        assertThat(response2.getBody().asString()).isEqualTo("User with this email already exists");

    }

    @EnabledIf(expression = "#{systemEnvironment['PROD'] == null}", reason = "Disabled in PROD environment")
    @Test
    void testFetchUser() {

        RestAssured.baseURI = "http://localhost:9090";

        String emailAddress = "test" + UUID.randomUUID() + "@example.com";

        given()
                .auth().basic("admin", "admin")
                .param("email", emailAddress)
                .param("password", "testPassword")
                .param("nickname", "NewUser")
                .when()
                .put("/user")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        Response response = given()
                .auth().basic("admin", "admin")
                .contentType("application/json")
                .body("""
                        {
                          "email": "%s",
                          "password": "testPassword"
                        }
                        """.formatted(emailAddress))
                .when()
                .post("/user")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        String userIdStr = response.path("user.id");
        String nickname = response.path("user.nickname");
        String returnedEmail = response.path("user.email");


        assertThat(nickname).isEqualTo("NewUser");
        assertThat(returnedEmail).isEqualTo(emailAddress);

        // Walidacja UUID
        UUID userId = UUID.fromString(userIdStr);
        assertThat(userId).isNotNull();
    }


}
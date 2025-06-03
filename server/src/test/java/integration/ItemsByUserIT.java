package integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("integration")
public class ItemsByUserIT {

    @EnabledIf(expression = "#{systemEnvironment['PROD'] == null}", reason = "Disabled in PROD environment")
    @Test
    void testItemsForUnknownUser_shouldReturnServerError() {
        RestAssured.baseURI = "http://localhost:9090";

        given()
                .auth().basic("admin", "admin")
                .param("email", "unknown-" + UUID.randomUUID() + "@example.com")
                .when()
                .post("/items/user")
                .then()
                .statusCode(500);
    }

    @EnabledIf(expression = "#{systemEnvironment['PROD'] == null}", reason = "Disabled in PROD environment")
    @Test
    void testItemsForNewUser_shouldBeEmpty() {
        RestAssured.baseURI = "http://localhost:9090";

        String email = "e2e" + UUID.randomUUID() + "@example.com";

        given()
                .auth().basic("admin", "admin")
                .contentType("application/x-www-form-urlencoded")
                .param("email", email)
                .param("password", "pass1234")
                .param("nickname", "E2ETest")
                .when()
                .put("/user")
                .then()
                .statusCode(200);

        Response response = given()
                .auth().basic("admin", "admin")
                .param("email", email)
                .when()
                .post("/items/user")
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<UUID> ids = response.jsonPath().getList(".", UUID.class);
        assertThat(ids).isEmpty();
    }
}

package integration;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static integration.IntegrationTestUtil.assumeServerRunning;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("integration")
public class ItemTagsIT {

    @EnabledIf(expression = "#{systemEnvironment['PROD'] == null}", reason = "Disabled in PROD environment")
    @Test
    void testSearchItemsByTags() {
        assumeServerRunning();
        RestAssured.baseURI = "http://localhost:9090";

        var response = given()
                .auth().basic("admin", "admin")
                .contentType("application/json")
                .body("{\"tags\":[\"#category=beer\"],\"matchAll\":true}")
                .when()
                .post("/items/tags")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertThat(response.getBody()).isNotNull();
    }
}

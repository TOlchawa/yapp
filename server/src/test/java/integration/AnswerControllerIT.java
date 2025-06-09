package integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.UUID;

import static integration.IntegrationTestUtil.assumeServerRunning;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("integration")
public class AnswerControllerIT {

    @EnabledIf(expression = "#{systemEnvironment['PROD'] == null}", reason = "Disabled in PROD environment")
    @Test
    void testAddAnswer() {
        assumeServerRunning();
        RestAssured.baseURI = "http://127.0.0.1:9090";

        Response response = given()
                .auth().basic("admin", "admin")
                .contentType("application/x-www-form-urlencoded")
                .param("questionId", UUID.randomUUID().toString())
                .param("fromUserId", UUID.randomUUID().toString())
                .param("text", "Integration answer")
                .when()
                .post("/answer")
                .then()
                .statusCode(200)
                .extract()
                .response();

        UUID id = response.as(UUID.class);
        assertThat(id).isNotNull();
    }
}

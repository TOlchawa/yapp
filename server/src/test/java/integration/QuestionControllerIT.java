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
public class QuestionControllerIT {

    @EnabledIf(expression = "#{systemEnvironment['PROD'] == null}", reason = "Disabled in PROD environment")
    @Test
    void testAskQuestion() {
        assumeServerRunning();
        RestAssured.baseURI = "http://127.0.0.1:9090";

        Response response = given()
                .auth().basic("admin", "admin")
                .contentType("application/x-www-form-urlencoded")
                .param("fromUserId", UUID.randomUUID().toString())
                .param("question", "Integration test question")
                .param("audience", "EVERYONE")
                .when()
                .post("/question")
                .then()
                .statusCode(200)
                .extract()
                .response();

        UUID id = response.as(UUID.class);
        assertThat(id).isNotNull();
    }
}

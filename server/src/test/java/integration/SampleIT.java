package integration;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static integration.IntegrationTestUtil.assumeServerRunning;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@Tag("integration")
public class SampleIT {

    @Test
    void testIntegrationScenario() {
        assumeServerRunning();
        RestAssured.baseURI = "http://localhost:9090";

        given()
                .auth().basic("admin", "admin")
                .when().get("/version")
                .then()
                .statusCode(200)
                .body(equalTo("1.0.0"))
                .log().all();;

    }

}

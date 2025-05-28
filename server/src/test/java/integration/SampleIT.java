package integration;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@Tag("integration")
public class SampleIT {

    @Test
    void testIntegrationScenario() {

        RestAssured.baseURI = "http://memoritta.com:19991";

        given()
                .auth().basic("user1", "1resu")
                .when().get("/version")
                .then()
                .statusCode(200)
                .body(equalTo("1.0.0"))
                .log().all();;

    }

}

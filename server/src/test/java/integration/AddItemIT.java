package integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("integration")
public class AddItemIT {

    @Test
    void testPingEndpoint() {
        RestAssured.baseURI = "http://localhost:9090"; // Ensure correct port

        Response response = given()
                .auth().basic("admin", "admin") // ✅ Ensure authentication
                .contentType("application/x-www-form-urlencoded") // ✅ Form data
                .param("ping", "test_ping") // ✅ Required parameter
                .when()
                .post("/ping")
                .then()
                .statusCode(200) // ✅ Expect 200 OK
                .contentType("application/json") // ✅ Expect JSON response
                .extract()
                .response();

        // Extract UUID from response
        UUID uuidResult = response.as(UUID.class);
        assertThat(uuidResult).isNotNull(); // ✅ Validate response is a UUID
    }


    @Disabled // TODO
    // make it work in github flow - localy it is working fine.
    @Test
    void testAddItem() {

        RestAssured.baseURI = "http://localhost:9090";

        File file = new File("src/test/resources/picture.jpg");

        assertThat(file.exists()).isTrue();

        Response response = given()
                .auth().basic("admin", "admin")
                .multiPart("picture", file)
                .param("name", "sample name")
                .param("note", "sample note")
                .param("barCode", "1234567abcd")
                .when()
                .post("/item")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        Response finalResponseReturn = response.andReturn();
        UUID uuidResult = finalResponseReturn.as(UUID.class);
        assertThat(uuidResult).isNotNull();
    }

}

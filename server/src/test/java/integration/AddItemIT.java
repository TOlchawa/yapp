package integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.EnabledIf;

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

    // test logic that should not run on GitHub Actions
    @EnabledIf(expression = "#{systemEnvironment['PROD'] == null}", reason = "Disabled in PROD environment")
    @Test
    void testAddItem() {

        RestAssured.baseURI = "http://127.0.0.1:9090";

        File file = new File("src/test/resources/picture.jpg");

        assertThat(file.exists()).isTrue();

        Response response = given()
                .auth().basic("admin", "admin")
                .multiPart("picture", file)
                .param("name", "sample name")
                .param("note", "sample note")
                .param("barCode", "1234567")
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

    @EnabledIf(expression = "#{systemEnvironment['PROD'] == null}", reason = "Disabled in PROD environment")
    @Test
    void testAddItemWithBase64() throws Exception {

        RestAssured.baseURI = "http://127.0.0.1:9090";

        File file = new File("src/test/resources/picture.jpg");
        assertThat(file.exists()).isTrue();

        byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
        String encoded = java.util.Base64.getEncoder().encodeToString(bytes);

        Response response = given()
                .auth().basic("admin", "admin")
                .param("name", "base64 name")
                .param("note", "base64 note")
                .param("pictureBase64", encoded)
                .when()
                .post("/item")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        UUID uuidResult = response.as(UUID.class);
        assertThat(uuidResult).isNotNull();
    }
}

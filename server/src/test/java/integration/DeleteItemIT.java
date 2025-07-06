package integration;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.io.File;
import java.util.UUID;

import static integration.IntegrationTestUtil.assumeServerRunning;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("integration")
public class DeleteItemIT {

    @EnabledIf(expression = "#{systemEnvironment['PROD'] == null}", reason = "Disabled in PROD environment")
    @Test
    void testDeleteItem() throws Exception {
        assumeServerRunning();
        RestAssured.baseURI = "http://127.0.0.1:9090";

        File file = new File("src/test/resources/picture.jpg");
        assertThat(file.exists()).isTrue();

        UUID createdId = given()
                .auth().basic("admin", "admin")
                .multiPart("picture", file)
                .param("name", "delete item")
                .when()
                .post("/item")
                .then()
                .statusCode(200)
                .extract()
                .as(UUID.class);

        given()
                .auth().basic("admin", "admin")
                .when()
                .delete("/item/" + createdId)
                .then()
                .statusCode(200);
    }
}

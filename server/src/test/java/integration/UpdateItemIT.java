package integration;

import com.memoritta.server.model.Item;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static integration.IntegrationTestUtil.assumeServerRunning;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("integration")
public class UpdateItemIT {

    @EnabledIf(expression = "#{systemEnvironment['PROD'] == null}", reason = "Disabled in PROD environment")
    @Test
    void testUpdateItemWithBase64() throws Exception {
        assumeServerRunning();
        RestAssured.baseURI = "http://127.0.0.1:9090";

        File file = new File("src/test/resources/picture.jpg");
        assertThat(file.exists()).isTrue();

        UUID createdId = given()
                .auth().basic("admin", "admin")
                .multiPart("picture", file)
                .param("name", "update item")
                .when()
                .post("/item")
                .then()
                .statusCode(200)
                .extract()
                .as(UUID.class);

        String encoded = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR4nGNgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII=";

        Item updated = given()
                .auth().basic("admin", "admin")
                .param("id", createdId.toString())
                .param("pictureBase64", encoded)
                .when()
                .put("/item")
                .then()
                .statusCode(200)
                .extract()
                .as(Item.class);

        assertThat(updated.getId()).isEqualTo(createdId);
    }
}

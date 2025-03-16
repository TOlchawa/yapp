package integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
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
    void testAddItem() {

        RestAssured.baseURI = "http://localhost:9090";

        File file = new File("src/test/resources/picture.jpg");

        Response response = given()
                .multiPart("picture", file)
                .param("name", "sample name")
                .param("note", "sample note")
                .param("barCode", "1234567abcd")
                .when()
                .post("/registerItem")
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

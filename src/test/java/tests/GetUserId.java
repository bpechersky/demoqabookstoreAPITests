package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class GetUserId {

    @Test
    public void createUserAndPrintUserId() {
        RestAssured.baseURI = "https://demoqa.com";

        //String username = "user" + System.currentTimeMillis();
        String username = "bpechersky";
        String password = "Budman1967!!!";

        String payload = String.format("{\"userName\":\"%s\", \"password\":\"%s\"}", username, password);

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/Account/v1/User")
                .then()
                .statusCode(201)
                .extract().response();

        String userId = response.jsonPath().getString("userID");
        System.out.println("Created user: " + username);
        System.out.println("User ID: " + userId);
    }
}


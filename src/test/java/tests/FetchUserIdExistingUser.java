package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class FetchUserIdExistingUser {

    @Test
    public void fetchUserId() {
        String username = "bpechersky";
        String password = "Budman1967!!!";

        RestAssured.baseURI = "https://demoqa.com";

        // 1. Generate token
        String tokenPayload = String.format("{ \"userName\": \"%s\", \"password\": \"%s\" }", username, password);

        Response tokenResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(tokenPayload)
                .when()
                .post("/Account/v1/GenerateToken")
                .then()
                .statusCode(200)
                .extract().response();

        String token = tokenResponse.jsonPath().getString("token");

        // 2. Trigger error to expose userId
        String fakeIsbnPayload = String.format("""
            {
              "userId": "00000000-0000-0000-0000-000000000000",
              "collectionOfIsbns": [
                { "isbn": "9781449325862" }
              ]
            }
        """);

        Response addBookResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(fakeIsbnPayload)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .extract().response();

        String message = addBookResponse.jsonPath().getString("message");
        String actualUserId = null;

        if (message != null && message.contains("ISBN supplied is not available in User's Collection")) {
            // Optional: Log the message
            System.out.println("❗ Message: " + message);
        } else {
            // Sometimes API leaks userID in error response body (edge case)
            actualUserId = addBookResponse.jsonPath().getString("userId");
        }

        System.out.println("🪪 Auth token: " + token);
        System.out.println("🆔 (If leaked) User ID: " + actualUserId);
    }
}


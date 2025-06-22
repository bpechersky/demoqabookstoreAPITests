package utils;

import io.restassured.http.ContentType;
import static io.restassured.RestAssured.*;

public class TokenManager {
    public static String getToken(String user, String pass) {
        return given()
                .contentType(ContentType.JSON)
                .body("{ \"userName\": \"" + user + "\", \"password\": \"" + pass + "\" }")
                .post("/Account/v1/GenerateToken")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}

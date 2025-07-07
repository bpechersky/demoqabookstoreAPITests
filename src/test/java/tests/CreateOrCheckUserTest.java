package tests;



import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class CreateOrCheckUserTest {

    @Test
    public void createOrCheckUser() {
        RestAssured.baseURI = "https://demoqa.com";

        String username = "bpechersky";
        String password = "Budman1967!!!";

        String payload = String.format("{ \"userName\": \"%s\", \"password\": \"%s\" }", username, password);

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/Account/v1/User")
                .then()
                .extract().response();

        if (createResponse.statusCode() == 201) {
            String userId = createResponse.jsonPath().getString("userID");
            System.out.println("✅ Created userId: " + userId);
        } else {
            System.out.println("⚠️ User already exists. You'll need to reuse the saved userId.");
            System.out.println("🔁 Response: " + createResponse.asString());
        }
    }
}

package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class AddBookUpdateBookDeleteAllBooksFromUserCollection {

    private static final String BASE_URL = "https://demoqa.com";
    private static final String USER_ID = "bc8d7642-7a52-45ab-b188-5ecbb4484937";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImJwZWNoZXJza3kxMTEiLCJwYXNzd29yZCI6IkJ1ZG1hbjE5NjchISEiLCJpYXQiOjE3NTE5MTA5NjZ9.4aNIfK48CyV_tKDlm3tyOcpzHSuK1KboMh72MGSn39Q";
    private static final String ISBN = "9781449331818";

    @Test(priority = 1)
    public void addBookToUserCollection() {
        RestAssured.baseURI = BASE_URL;

        JSONObject book = new JSONObject().put("isbn", ISBN);
        JSONArray collection = new JSONArray().put(book);

        JSONObject requestBody = new JSONObject()
                .put("userId", USER_ID)
                .put("collectionOfIsbns", collection);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + TOKEN)
                .body(requestBody.toString())
                .when()
                .post("/BookStore/v1/Books");

        System.out.println("Add Book Response:\n" + response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 201);
    }
    @Test(priority = 2)
    public void updateBookIsbnTest() {
        String userId = "bc8d7642-7a52-45ab-b188-5ecbb4484937";
        String oldIsbn = "9781449331818";
        String newIsbn = "9781449325862";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImJwZWNoZXJza3kxMTEiLCJwYXNzd29yZCI6IkJ1ZG1hbjE5NjchISEiLCJpYXQiOjE3NTE5MTA5NjZ9.4aNIfK48CyV_tKDlm3tyOcpzHSuK1KboMh72MGSn39Q";

        String payload = """
        {
          "userId": "%s",
          "isbn": "%s"
        }
        """.formatted(userId, newIsbn);

        given()
                .baseUri("https://demoqa.com")
                .basePath("/BookStore/v1/Books/" + oldIsbn)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .body(payload)
                .when()
                .put()
                .then()
                .statusCode(200); // OK
    }

    @Test(priority = 3)
    public void deleteAllBooksTest() {
        String userId = "bc8d7642-7a52-45ab-b188-5ecbb4484937";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImJwZWNoZXJza3kxMTEiLCJwYXNzd29yZCI6IkJ1ZG1hbjE5NjchISEiLCJpYXQiOjE3NTE5MTA5NjZ9.4aNIfK48CyV_tKDlm3tyOcpzHSuK1KboMh72MGSn39Q";
        String basicAuth = "Basic YnBlY2hlcnNreTExMTpCdWRtYW4xOTY3ISEh"; // optional unless required by API

        given()
                .baseUri("https://demoqa.com")
                .basePath("/BookStore/v1/Books")
                .queryParam("UserId", userId)
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .header("authorization", basicAuth) // usually unnecessary if Bearer is accepted
                .when()
                .delete()
                .then()
                .statusCode(204); // No Content
    }


}


package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class AddBookUpdateBookDeleteAllBooksFromUserCollection {

    private static final String BASE_URL = "https://demoqa.com";
    private static final String USER_ID = "bc8d7642-7a52-45ab-b188-5ecbb4484937";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImJwZWNoZXJza3kxMTEiLCJwYXNzd29yZCI6IkJ1ZG1hbjE5NjchISEiLCJpYXQiOjE3NTE5MTA5NjZ9.4aNIfK48CyV_tKDlm3tyOcpzHSuK1KboMh72MGSn39Q";
    private static final String ISBN = "9781449331818";

    @Test(priority = 1)
    public void addBookTest() {

        String payload = """
        {
          "userId": "%s",
          "collectionOfIsbns": [
            {
              "isbn": "%s"
            }
          ]
        }
        """.formatted(USER_ID, ISBN);

        Response response = given()
                .baseUri("https://demoqa.com")
                .basePath("/BookStore/v1/Books")
                .header("Authorization", "Bearer " + TOKEN)
                .contentType("application/json")
                .body(payload)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().response();

        // Validate response body
        String responseUserId = response.jsonPath().getString("userId");
        List<String> isbns = response.jsonPath().getList("books.isbn");

        Assert.assertTrue(isbns.contains(ISBN), "ISBN should be in the response");
    }


    @Test(priority = 2)
    public void updateBookIsbnTestEnhanced() {
        String userId = "bc8d7642-7a52-45ab-b188-5ecbb4484937";
        String username = "bpechersky111";
        String oldIsbn = "9781449331818";
        String newIsbn = "9781449325862";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImJwZWNoZXJza3kxMTEiLCJwYXNzd29yZCI6IkJ1ZG1hbjE5NjchISEiLCJpYXQiOjE3NTE5MTA5NjZ9.4aNIfK48CyV_tKDlm3tyOcpzHSuK1KboMh72MGSn39Q";

        String payload = """
        {
          "userId": "%s",
          "isbn": "%s"
        }
        """.formatted(userId, newIsbn);

        Response response = given()
                .baseUri("https://demoqa.com")
                .basePath("/BookStore/v1/Books/" + oldIsbn)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .header("accept", "application/json")
                .body(payload)
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract().response();

        // Response Body Validation
        String actualUserId = response.jsonPath().getString("userId");
        String actualUsername = response.jsonPath().getString("username");
        String actualIsbn = response.jsonPath().getString("books[0].isbn");
        String title = response.jsonPath().getString("books[0].title");
        String author = response.jsonPath().getString("books[0].author");
        int pages = response.jsonPath().getInt("books[0].pages");

        Assert.assertEquals(actualUserId, userId, "User ID should match");
        Assert.assertEquals(actualUsername, username, "Username should match");
        Assert.assertEquals(actualIsbn, newIsbn, "ISBN should be updated");
        Assert.assertEquals(title, "Git Pocket Guide", "Title should match");
        Assert.assertEquals(author, "Richard E. Silverman", "Author should match");
        Assert.assertEquals(pages, 234, "Page count should match");
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


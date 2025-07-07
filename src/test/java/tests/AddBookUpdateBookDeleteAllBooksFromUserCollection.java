package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AddBookUpdateBookDeleteAllBooksFromUserCollection {

    private static final String BASE_URL = "https://demoqa.com";
    private static final String USER_ID = "bc8d7642-7a52-45ab-b188-5ecbb4484937";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImJwZWNoZXJza3kxMTEiLCJwYXNzd29yZCI6IkJ1ZG1hbjE5NjchISEiLCJpYXQiOjE3NTE5MTc0MzJ9.IIcIk6C4J4MEvqwKLfCfR5zBD_evOl5jlmxLJXzEEeU";
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
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImJwZWNoZXJza3kxMTEiLCJwYXNzd29yZCI6IkJ1ZG1hbjE5NjchISEiLCJpYXQiOjE3NTE5MTc0MzJ9.IIcIk6C4J4MEvqwKLfCfR5zBD_evOl5jlmxLJXzEEeU";

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
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImJwZWNoZXJza3kxMTEiLCJwYXNzd29yZCI6IkJ1ZG1hbjE5NjchISEiLCJpYXQiOjE3NTE5MTc0MzJ9.IIcIk6C4J4MEvqwKLfCfR5zBD_evOl5jlmxLJXzEEeU";
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

    @Test(priority = 4)
    public void getAllBooksWithFullValidationTest() {
        Response response = given()
                .baseUri("https://demoqa.com")
                .basePath("/BookStore/v1/Books")
                .header("accept", "application/json")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        // Parse response as list of books
        List<Map<String, Object>> books = response.jsonPath().getList("books");

        // Validate overall response
        Assert.assertNotNull(books, "Books list should not be null");
        Assert.assertEquals(books.size(), 8, "Expected 8 books in the catalog");

        // Validate specific book by ISBN
        Map<String, Object> book1 = books.stream()
                .filter(book -> "9781449325862".equals(book.get("isbn")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected book with ISBN 9781449325862 not found"));

        Assert.assertEquals(book1.get("title"), "Git Pocket Guide");
        Assert.assertEquals(book1.get("author"), "Richard E. Silverman");
        Assert.assertEquals(book1.get("pages"), 234);
        Assert.assertEquals(book1.get("publisher"), "O'Reilly Media");

        // Validate another known book
        Map<String, Object> book2 = books.stream()
                .filter(book -> "9781491904244".equals(book.get("isbn")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected book with ISBN 9781491904244 not found"));

        Assert.assertEquals(book2.get("title"), "You Don't Know JS");
        Assert.assertEquals(book2.get("author"), "Kyle Simpson");
        Assert.assertEquals(book2.get("publisher"), "O'Reilly Media");
    }

    @Test(priority = 5)
    public void getBookByIsbnTest() {
        String isbn = "9781449331818";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                + "IIcIk6C4J4MEvqwKLfCfR5zBD_evOl5jlmxLJXzEEeU";
        String basicAuth = "Basic YnBlY2hlcnNreTExMTpCdWRtYW4xOTY3ISEh";

        Response response = given()
                .baseUri("https://demoqa.com")
                .basePath("/BookStore/v1/Book")
                .queryParam("ISBN", isbn)
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .header("authorization", basicAuth)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        // Validate response body content
        Assert.assertEquals(response.jsonPath().getString("isbn"), isbn);
        Assert.assertEquals(response.jsonPath().getString("title"), "Learning JavaScript Design Patterns");
        Assert.assertEquals(response.jsonPath().getString("subTitle"), "A JavaScript and jQuery Developer's Guide");
        Assert.assertEquals(response.jsonPath().getString("author"), "Addy Osmani");
        Assert.assertEquals(response.jsonPath().getString("publisher"), "O'Reilly Media");
        Assert.assertEquals(response.jsonPath().getInt("pages"), 254);
        Assert.assertTrue(response.jsonPath().getString("website")
                .contains("addyosmani.com/resources/essentialjsdesignpatterns"), "Website should match expected domain");
    }


}


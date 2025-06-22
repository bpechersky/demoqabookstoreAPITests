package tests;

import base.TestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.BookCollection;
import models.Isbn;
import models.User;
import org.testng.annotations.Test;
import utils.PayloadBuilder;
import utils.TokenManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Epic("Bookstore API")
@Feature("User book flow")
public class UserBookFlowTests extends TestBase {

    private static String generatedUsername;
    private static String generatedUserId;

    @Test(priority = 1, description = "Create a user using JSON file")
    @Story("Create User")
    @Severity(SeverityLevel.CRITICAL)
    public void createUser() throws IOException {
        JsonNode userData = new ObjectMapper().readTree(new File("src/test/resources/data/users.json")).get(0);
        generatedUsername = userData.get("userName").asText() + System.currentTimeMillis();
        String password = userData.get("password").asText();

        User user = new User(generatedUsername, password);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/Account/v1/User");

        response.then()
            .statusCode(201)
            .body("username", equalTo(generatedUsername));

        generatedUserId = response.jsonPath().getString("userID");
    }

    @Test(priority = 2, dependsOnMethods = "createUser", description = "Add and delete book using external data")
    @Story("Manage Books")
    @Severity(SeverityLevel.NORMAL)
    public void addAndDeleteBook() throws Exception {
        JsonNode userData = new ObjectMapper().readTree(new File("src/test/resources/data/users.json")).get(0);
        JsonNode bookData = new ObjectMapper().readTree(new File("src/test/resources/data/books.json")).get(0);

        String password = userData.get("password").asText();
        String isbn = bookData.get("isbn").asText();

        String token = TokenManager.getToken(generatedUsername, password);
        BookCollection collection = new BookCollection(generatedUserId, List.of(new Isbn(isbn)));

        given()
            .contentType(ContentType.JSON)
            .auth().oauth2(token)
            .body(collection)
        .when()
            .post("/BookStore/v1/Books")
        .then()
            .statusCode(201);

        Thread.sleep(1000);

        given()
            .auth().oauth2(token)
        .when()
            .get("/Account/v1/User/" + generatedUserId)
        .then()
            .statusCode(200)
            .body("books.isbn", hasItem(isbn));

        String deletePayload = PayloadBuilder.buildDeletePayload(isbn, generatedUserId);

        given()
            .contentType(ContentType.JSON)
            .auth().oauth2(token)
            .body(deletePayload)
        .when()
            .delete("/BookStore/v1/Book")
        .then()
            .log().all()
            .statusCode(204);
    }
}

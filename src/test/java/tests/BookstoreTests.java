package tests;

import base.TestBase;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class BookstoreTests extends TestBase {

    @Test
    public void getAllBooks_shouldReturnList() {
        given()
        .when()
            .get("/BookStore/v1/Books")
        .then()
            .statusCode(200)
            .body("books.size()", greaterThan(0));
    }

    @Test
    public void searchBookByISBN_shouldReturnBook() {
        String isbn = "9781449325862";

        given()
        .when()
            .get("/BookStore/v1/Book?ISBN=" + isbn)
        .then()
            .statusCode(200)
            .body("isbn", equalTo(isbn));
    }
}

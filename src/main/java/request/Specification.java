package request;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;

import static com.jayway.restassured.RestAssured.given;

class Specification {

    <T> RequestSpecification getSpecification(T data, String token) {
        return given()
                .contentType(ContentType.JSON)
                .body(data.toString())
                .header("Authorization", token)
                .when();
    }

    RequestSpecification getSpecification(String token) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .when();
    }

    <T> RequestSpecification getSpecification(T data) {
        return given()
                .contentType(ContentType.JSON)
                .body(data.toString())
                .when();
    }

    RequestSpecification getSpecification() {
        return given()
                .contentType(ContentType.JSON)
                .when();
    }
}

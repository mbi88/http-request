package request;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;

import static com.jayway.restassured.RestAssured.given;

class Specification {

    private RequestSpecification getRequestSpecification() {
        return given().contentType(ContentType.JSON);
    }

    <T> RequestSpecification getSpecification(T data, String token) {
        return getRequestSpecification()
                .body(data.toString())
                .header("Authorization", token)
                .when();
    }

    RequestSpecification getSpecification(String token) {
        return getRequestSpecification()
                .header("Authorization", token)
                .when();
    }

    <T> RequestSpecification getSpecification(T data) {
        return getRequestSpecification()
                .body(data.toString())
                .when();
    }

    RequestSpecification getSpecification() {
        return getRequestSpecification().when();
    }
}

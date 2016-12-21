package request;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;

public class HttpRequest implements Request {

    @Override
    public Response get(String url, int statusCode, String token) {
        Response r = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .get(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response get(String url, String token) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .get(url);
    }

    @Override
    public Response get(String url, int statusCode) {
        Response r = given()
                .contentType(ContentType.JSON)
                .when()
                .get(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response post(String url, T data, int statusCode, String token) {
        Response r = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(data.toString())
                .when()
                .post(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response post(String url, T data, String token) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(data.toString())
                .when()
                .post(url);
    }

    @Override
    public <T> Response post(String url, T data, int statusCode) {
        Response r = given()
                .contentType(ContentType.JSON)
                .body(data.toString())
                .when()
                .post(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response post(String url, int statusCode, String token) {
        Response r = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .post(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response put(String url, T data, int statusCode, String token) {
        Response r = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(data.toString())
                .when()
                .put(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response put(String url, T data, String token) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(data.toString())
                .when()
                .put(url);
    }

    @Override
    public <T> Response put(String url, T data, int statusCode) {
        Response r = given()
                .contentType(ContentType.JSON)
                .body(data.toString())
                .when()
                .put(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response put(String url, int statusCode, String token) {
        Response r = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .put(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response patch(String url, T data, int statusCode, String token) {
        Response r = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(data.toString())
                .when()
                .patch(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response patch(String url, T data, String token) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(data.toString())
                .when()
                .patch(url);
    }

    @Override
    public <T> Response patch(String url, T data, int statusCode) {
        Response r = given()
                .contentType(ContentType.JSON)
                .body(data.toString())
                .when()
                .patch(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response patch(String url, int statusCode, String token) {
        Response r = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .patch(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response delete(String url, T data, int statusCode, String token) {
        Response r = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(data.toString())
                .when()
                .delete(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response delete(String url, int statusCode, String token) {
        Response r = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .delete(url);

        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response delete(String url, String token) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .when()
                .delete(url);
    }

    @Override
    public Response delete(String url, int statusCode) {
        Response r = given()
                .contentType(ContentType.JSON)
                .when()
                .delete(url);

        checkStatus(r, statusCode);

        return r;
    }
}

package methods;

import com.jayway.restassured.response.Response;

interface Patch {

    <T> Response patch(String url, T data, int statusCode, String token);

    <T> Response patch(String url, T data, String token);

    <T> Response patch(String url, T data, int statusCode);

    Response patch(String url, int statusCode, String token);
}

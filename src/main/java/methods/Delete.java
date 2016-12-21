package methods;

import com.jayway.restassured.response.Response;

interface Delete {

    <T> Response delete(String path, T data, int statusCode, String token);

    Response delete(String path, int statusCode, String token);

    Response delete(String path, String token);

    Response delete(String path, int statusCode);
}

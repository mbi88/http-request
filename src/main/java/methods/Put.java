package methods;

import com.jayway.restassured.response.Response;

interface Put {

    <T> Response put(String path, T data, int statusCode, String token);

    <T> Response put(String path, T data, String token);

    <T> Response put(String path, T data, int statusCode);

    Response put(String path, int statusCode, String token);
}

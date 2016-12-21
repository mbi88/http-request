package methods;

import com.jayway.restassured.response.Response;

public interface Post {

    <T> Response post(String url, T data, int statusCode, String token);

    <T> Response post(String url, T data, String token);

    <T> Response post(String url, T data, int statusCode);

    Response post(String url, int statusCode, String token);
}

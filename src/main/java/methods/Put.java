package methods;

import com.jayway.restassured.response.Response;

public interface Put {

    <T> Response put(String url, T data, int statusCode, String token);

    <T> Response put(String url, T data, String token);

    <T> Response put(String url, T data, int statusCode);

    Response put(String url, int statusCode, String token);
}

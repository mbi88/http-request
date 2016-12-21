package methods;

import com.jayway.restassured.response.Response;

public interface Delete {

    <T> Response delete(String url, T data, int statusCode, String token);

    Response delete(String url, int statusCode, String token);

    Response delete(String url, String token);

    Response delete(String url, int statusCode);
}

package methods;

import com.jayway.restassured.response.Response;

interface Get {

    Response get(String url, int statusCode, String token);

    Response get(String url, String token);

    Response get(String url, int statusCode);
}

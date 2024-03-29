package com.mbi;

import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

/**
 * Performs http requests.
 * Checks response status code equals to expected.
 * Error message contains url, response and request as a curl.
 * Resets builder to default after each request.
 */
@SuppressWarnings("PMD.LinguisticNaming")
public interface HttpRequest {

    /**
     * Sets a header.
     *
     * @param header header name.
     * @param value  header value.
     * @return HttpRequest.
     */
    HttpRequest setHeader(String header, String value);

    /**
     * Sets headers as a list.
     *
     * @param headers headers list.
     * @return HttpRequest.
     */
    HttpRequest setHeaders(List<Header> headers);

    /**
     * Sets request body.
     *
     * @param data data for request.
     * @return HttpRequest.
     */
    HttpRequest setData(Object data);

    /**
     * Sets request token in Authentication header.
     *
     * @param token auth token.
     * @return HttpRequest.
     */
    HttpRequest setToken(String token);

    /**
     * Sets expected status code in response.
     *
     * @param code expected code.
     * @return HttpRequest.
     */
    HttpRequest setExpectedStatusCode(Integer code);

    /**
     * Sets expected status codes in response .
     *
     * @param codes expected code.
     * @return HttpRequest.
     */
    HttpRequest setExpectedStatusCodes(List<Integer> codes);

    /**
     * Sets if response should contain 'errors' array.
     *
     * @param noErrors errors flag.
     * @return HttpRequest.
     */
    HttpRequest checkNoErrors(Boolean noErrors);

    /**
     * Sets custom request specification.
     *
     * @param specification request specification.
     * @return HttpRequest.
     */
    HttpRequest setRequestSpecification(RequestSpecification specification);

    /**
     * Sets request url.
     *
     * @param url request url.
     * @return HttpRequest.
     */
    HttpRequest setUrl(String url);

    /**
     * Prints request debug info.
     *
     * @return HttpRequest,
     */
    HttpRequest debug();

    /**
     * Performs post request.
     *
     * @param url        request url.
     * @param pathParams path parameters.
     * @return response.
     */
    Response post(String url, Object... pathParams);

    /**
     * Performs get request.
     *
     * @param url        request url.
     * @param pathParams path parameters.
     * @return response.
     */
    Response get(String url, Object... pathParams);

    /**
     * Performs put request.
     *
     * @param url        request url.
     * @param pathParams path parameters.
     * @return response.
     */
    Response put(String url, Object... pathParams);

    /**
     * Performs patch request.
     *
     * @param url        request url.
     * @param pathParams path parameters.
     * @return response.
     */
    Response patch(String url, Object... pathParams);

    /**
     * Performs delete request.
     *
     * @param url        request url.
     * @param pathParams path parameters.
     * @return response.
     */
    Response delete(String url, Object... pathParams);
}

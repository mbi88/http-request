package com.mbi;

import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

/**
 * Fluent HTTP request interface for testing APIs.
 * <p>
 * Allows setting headers, body, authentication, and expected status codes.
 * Supports all major HTTP methods: GET, POST, PUT, PATCH, DELETE.
 * <p>
 * After each request, the builder is reset automatically.
 * If the response status or content is invalid, a detailed error message is thrown,
 * including the request curl and response body.
 */
@SuppressWarnings("PMD.LinguisticNaming")
public interface HttpRequest {

    /**
     * Adds a single header to the request.
     *
     * @param header Header name.
     * @param value  Header value.
     * @return current HttpRequest instance.
     */
    HttpRequest setHeader(String header, String value);

    /**
     * Adds multiple headers to the request.
     *
     * @param headers List of Rest-Assured headers.
     * @return current HttpRequest instance.
     */
    HttpRequest setHeaders(List<Header> headers);

    /**
     * Sets request body (object will be serialized using Rest-Assured's default mechanism).
     *
     * @param data body payload.
     * @return current HttpRequest instance.
     */
    HttpRequest setData(Object data);

    /**
     * Sets Bearer authentication token (sent as Authorization header).
     *
     * @param token bearer token value.
     * @return current HttpRequest instance.
     */
    HttpRequest setToken(String token);

    /**
     * Sets a single expected HTTP status code.
     *
     * @param code expected status code (e.g. 200, 201, etc.).
     * @return current HttpRequest instance.
     */
    HttpRequest setExpectedStatusCode(Integer code);

    /**
     * Sets multiple expected HTTP status codes.
     *
     * @param codes list of acceptable status codes.
     * @return current HttpRequest instance.
     */
    HttpRequest setExpectedStatusCodes(List<Integer> codes);

    /**
     * Indicates whether response should NOT contain "errors" array in body.
     *
     * @param noErrors true to check absence of errors; false to skip check.
     * @return current HttpRequest instance.
     */
    HttpRequest checkNoErrors(Boolean noErrors);

    /**
     * Sets a custom Rest-Assured specification.
     *
     * @param specification preconfigured Rest-Assured specification.
     * @return current HttpRequest instance.
     */
    HttpRequest setRequestSpecification(RequestSpecification specification);

    /**
     * Sets target request URL.
     *
     * @param url full endpoint URL (may contain path params).
     * @return current HttpRequest instance.
     */
    HttpRequest setUrl(String url);

    /**
     * Enables full request/response debug logging.
     *
     * @return current HttpRequest instance.
     */
    HttpRequest debug();

    /**
     * Executes a POST request to the specified URL.
     *
     * @param url        endpoint URL.
     * @param pathParams optional path parameters.
     * @return Rest-Assured response object.
     */
    Response post(String url, Object... pathParams);

    /**
     * Executes a GET request to the specified URL.
     *
     * @param url        endpoint URL.
     * @param pathParams optional path parameters.
     * @return Rest-Assured response object.
     */
    Response get(String url, Object... pathParams);

    /**
     * Executes a PUT request to the specified URL.
     *
     * @param url        endpoint URL.
     * @param pathParams optional path parameters.
     * @return Rest-Assured response object.
     */
    Response put(String url, Object... pathParams);

    /**
     * Executes a PATCH request to the specified URL.
     *
     * @param url        endpoint URL.
     * @param pathParams optional path parameters.
     * @return Rest-Assured response object.
     */
    Response patch(String url, Object... pathParams);

    /**
     * Executes a DELETE request to the specified URL.
     *
     * @param url        endpoint URL.
     * @param pathParams optional path parameters.
     * @return Rest-Assured response object.
     */
    Response delete(String url, Object... pathParams);
}

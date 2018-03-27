package com.mbi;

import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

/**
 *
 */
public interface HttpRequest {

    HttpRequest setHeader(String header, String value);

    HttpRequest setHeaders(List<Header> headers);

    HttpRequest setData(Object data);

    HttpRequest setToken(String token);

    HttpRequest setExpectedStatusCode(Integer code);

    HttpRequest setRequestSpecification(RequestSpecification specification);

    Response post(String url);

    Response get(String url);

    Response put(String url);

    Response patch(String url);

    Response delete(String url);
}

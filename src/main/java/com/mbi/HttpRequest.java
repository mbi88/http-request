package com.mbi;

import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public interface HttpRequest {

    HttpRequest setHeader(String header, String value);

    HttpRequest setHeader(List<Header> header);

    HttpRequest setData(Object data);

    HttpRequest setToken(String token);

    HttpRequest setExpectedStatusCode(int code);

    HttpRequest setRequestSpecification(RequestSpecification specification);

    HttpRequest setPath(String path);

    HttpRequest sesMethod(String method);

    Response post(String path);

    Response get(String path);

    Response put(String path);

    Response patch(String path);

    Response delete(String path);
}

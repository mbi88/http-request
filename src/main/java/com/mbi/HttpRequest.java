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

    HttpRequest setPath(String path);

    HttpRequest setRequestSpecification(RequestSpecification specification);

    Response post();

    Response get();

    Response put();

    Response patch();

    Response delete();
}

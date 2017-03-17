package com.mbi;

import com.jayway.restassured.specification.RequestSpecification;

public class Parameters {
    private final String path;
    private final Object data;
    private final int statusCode;
    private final String token;
    private final RequestSpecification specification;

    public Parameters(String path, Object data, int statusCode, String token, RequestSpecification specification) {
        this.path = path;
        this.data = data;
        this.statusCode = statusCode;
        this.token = token;
        this.specification = specification;
    }

    public String getPath() {
        return path;
    }

    public Object getData() {
        return data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getToken() {
        return token;
    }

    public RequestSpecification getSpecification() {
        return specification;
    }
}

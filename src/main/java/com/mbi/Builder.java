package com.mbi;

import com.mbi.http.HttpMethod;
import io.restassured.specification.RequestSpecification;

class Builder {
    private String path;
    private Object data;
    private int statusCode;
    private String token;
    private HttpMethod method;
    private RequestSpecification specification;
    private Parameters parameters;

    Builder setPath(String path) {
        this.path = path;
        return this;
    }

    Builder setData(Object data) {
        this.data = data;
        return this;
    }

    Builder setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    Builder setToken(String token) {
        this.token = token;
        return this;
    }

    Builder setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    Builder setSpecification(RequestSpecification specification) {
        this.specification = specification;
        return this;
    }

    HttpMethod getMethod() {
        return method;
    }

    Parameters getParameters() {
        return parameters;
    }

    void buildParameters() {
        this.parameters = new Parameters().newBuilder()
                .setPath(path)
                .setData(data)
                .setStatusCode(statusCode)
                .setToken(token)
                .setSpecification(specification)
                .build();
    }

    Request build() {
        return new Request(this);
    }
}

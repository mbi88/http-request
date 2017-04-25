package com.mbi;

import com.mbi.http.HttpMethod;
import io.restassured.response.Response;

public class Request {

    private final Parameters parameters;
    private final HttpMethod method;

    Request(Builder builder) {
        this.parameters = builder.getParameters();
        this.method = builder.getMethod();
    }

    public Parameters getParameters() {
        return parameters;
    }

    Response request() {
        return this.method.request(this);
    }
}

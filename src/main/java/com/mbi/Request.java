package com.mbi;

import com.jayway.restassured.response.Response;
import com.mbi.http.HttpMethod;

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

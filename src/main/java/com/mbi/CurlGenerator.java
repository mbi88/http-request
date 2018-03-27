package com.mbi;

import io.restassured.http.Header;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a curl.
 */
class CurlGenerator {

    private final RequestBuilder builder;
    private final RequestSpecification specification;

    CurlGenerator(final RequestBuilder builder, final RequestSpecification specification) {
        this.builder = builder;
        this.specification = specification;
    }

    public String getCurl() {
        return "curl"
                .concat(getMethod())
                .concat(getUrl())
                .concat(getHeaders())
                .concat(getData());
    }

    private String getMethod() {
        return String.format(" -X %s", builder.getMethod().toString());
    }

    private String getUrl() {
        return String.format(" '%s'", builder.getUrl());
    }

    private String getHeaders() {
        String headersStr = "";

        final FilterableRequestSpecification spec = (FilterableRequestSpecification) specification;
        final List<Header> headers = new ArrayList<>(spec.getHeaders().asList());
        for (Header header : headers) {
            headersStr = headersStr
                    .concat(" -H")
                    .concat(String.format(" '%s: %s'", header.getName(), header.getValue()));
        }

        return headersStr;
    }

    private String getData() {
        String data = "";

        final FilterableRequestSpecification spec = (FilterableRequestSpecification) specification;
        if (spec.getBody() != null) {
            data = String.format(" --data '%s'", spec.getBody().toString());
        }

        return data;
    }
}

package com.mbi;

import io.restassured.http.Header;
import io.restassured.specification.FilterableRequestSpecification;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a curl.
 */
class CurlGenerator {

    private final Configurator config;

    CurlGenerator(final Configurator config) {
        this.config = config;
    }

    public String getCurl() {
        return "curl"
                .concat(getMethod())
                .concat(getUrl())
                .concat(getHeaders())
                .concat(getData());
    }

    private String getMethod() {
        return String.format(" -X %s", config.getMethod());
    }

    private String getUrl() {
        return String.format(" '%s'", config.getUrl());
    }

    private String getHeaders() {
        String headersStr = "";

        final FilterableRequestSpecification spec = (FilterableRequestSpecification) config.getSpec();
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

        final FilterableRequestSpecification spec = (FilterableRequestSpecification) config.getSpec();
        if (spec.getBody() != null) {
            data = String.format(" --data '%s'", spec.getBody().toString());
        }

        return data;
    }
}

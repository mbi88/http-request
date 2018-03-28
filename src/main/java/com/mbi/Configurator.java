package com.mbi;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 *
 */
class Configurator {

    private final RequestBuilder builder;
    private RequestSpecification spec;

    Configurator(final RequestBuilder builder) {
        this.builder = builder;
        spec = configureRequest();
    }

    private RequestSpecification defaultSpecification() {
        return given()
                .contentType(ContentType.JSON)
                .accept("application/json");
    }

    public RequestSpecification getSpec() {
        return this.spec;
    }

    public RequestBuilder getBuilder() {
        return this.builder;
    }

    private RequestSpecification configureRequest() {
        spec = defaultSpecification();

        if (builder.getSpecification() != null) {
            spec.spec(builder.getSpecification());
        }

        if (builder.getToken() != null) {
            spec.header("Authorization", builder.getToken());
        }

        if (builder.getData() != null) {
            spec.body(builder.getData());
        }

        if (builder.getHeaders() != null) {
            for (Header header : builder.getHeaders()) {
                spec.header(header);
            }
        }

        return spec;
    }
}

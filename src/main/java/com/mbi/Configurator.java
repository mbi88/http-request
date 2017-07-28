package com.mbi;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Configurator {

    private RequestSpecification defaultSpecification() {
        return given()
                .contentType(ContentType.JSON)
                .accept("application/json");
    }

    public RequestSpecification configureRequest(RequestBuilder builder) {
        RequestSpecification spec = defaultSpecification();

        if (builder.getSpecification() != null)
            spec.spec(builder.getSpecification());

        if (builder.getToken() != null) {
            spec.header("Authorization", builder.getToken());
        }

        if (builder.getData() != null) {
            spec.body(builder.getData().toString());
        }

        if (builder.getHeaders() != null) {
            for (Header header : builder.getHeaders()) {
                spec.header(header);
            }
        }

        return spec;
    }
}

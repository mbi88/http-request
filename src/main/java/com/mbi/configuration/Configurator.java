package com.mbi.configuration;

import com.mbi.Parameters;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Configurator {

    private RequestSpecification defaultSpecification() {
        return given()
                .contentType(ContentType.JSON);
    }

    public RequestSpecification configureRequest(Parameters requestParameters) {
        RequestSpecification spec = defaultSpecification();

        if (requestParameters.getSpecification() != null)
            spec.spec(requestParameters.getSpecification());

        if (requestParameters.getToken() != null) {
            spec.header("Authorization", requestParameters.getToken());
        }

        if (requestParameters.getData() != null) {
            spec.body(requestParameters.getData().toString());
        }

        return spec;
    }
}

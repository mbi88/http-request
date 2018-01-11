package com.mbi.requestmethod;

import com.mbi.Configurator;
import com.mbi.OnRequestPerformedListener;
import com.mbi.RequestBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Objects;

public interface HttpRequestMethod {

    default RequestSpecification getSpecification(RequestBuilder builder) {
        return new Configurator().configureRequest(builder);
    }

    default void checkStatusCode(Response r, RequestBuilder builder) {
        // No need to check status code if it's not set
        if (Objects.isNull(builder.getStatusCode()))
            return;

        try {
            r.then().assertThat().statusCode(builder.getStatusCode());
        } catch (AssertionError ae) {
            throw new AssertionError(ae.getMessage()
                    .concat("\n")
                    .concat("Path: " + builder.getPath())
                    .concat("\n\n")
                    .concat("Response: " + r.asString()));
        }
    }

    Response request(RequestBuilder builder);

    void setRequestListener(OnRequestPerformedListener requestListener);
}

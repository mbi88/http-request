package com.mbi.http;


import com.mbi.Parameters;
import com.mbi.Request;
import com.mbi.configuration.Configurator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public interface HttpMethod {

    default RequestSpecification getSpecification(Parameters requestParameters) {
        return new Configurator().configureRequest(requestParameters);
    }

    default void checkStatusCode(Response r, Parameters requestParameters) {
        // No need to check status code if it's not set
        if (requestParameters.getStatusCode() == 0)
            return;

        try {
            r.then().assertThat().statusCode(requestParameters.getStatusCode());
        } catch (AssertionError ae) {
            throw new AssertionError(ae.getMessage().concat("\n").concat(r.asString()));
        }
    }

    Response request(Request request);
}

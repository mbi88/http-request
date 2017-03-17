package com.mbi.http;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.mbi.Parameters;
import com.mbi.Request;
import com.mbi.configuration.Configurator;

public interface HttpMethod {

    default RequestSpecification getSpecification(Parameters requestParameters) {
        return new Configurator().configureRequest(requestParameters);
    }

    default void checkStatusCode(Response r, Parameters requestParameters) {
        if (requestParameters.getStatusCode() != 0)
            try {
                r.then().assertThat().statusCode(requestParameters.getStatusCode());
            } catch (AssertionError ae) {
                throw new AssertionError(ae.getMessage().concat("\n").concat(r.asString()));
            }
    }

    Response request(Request request);
}

package com.mbi.request;

import com.jayway.restassured.response.Response;
import com.mbi.methods.Method;

interface Request extends Method {

    default void checkStatus(Response r, int statusCode) {
        try {
            r.then().assertThat().statusCode(statusCode);
        } catch (AssertionError ae) {
            throw new AssertionError(ae.getMessage().concat("\n").concat(r.asString()));
        }
    }
}

package com.mbi.http;

import io.restassured.response.Response;
import com.mbi.Parameters;
import com.mbi.Request;

public class PutHttpMethod implements HttpMethod {

    @Override
    public Response request(Request request) {
        Parameters params = request.getParameters();
        Response r = getSpecification(params).put(params.getPath());
        checkStatusCode(r, params);

        return r;
    }
}

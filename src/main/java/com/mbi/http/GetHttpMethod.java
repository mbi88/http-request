package com.mbi.http;

import io.restassured.response.Response;
import com.mbi.Parameters;
import com.mbi.Request;

public class GetHttpMethod implements HttpMethod {

    @Override
    public Response request(Request request) {
        Parameters params = request.getParameters();
        Response r = getSpecification(params).get(params.getPath());
        checkStatusCode(r, params);

        return r;
    }
}

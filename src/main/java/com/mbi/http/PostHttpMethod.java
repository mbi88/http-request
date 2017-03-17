package com.mbi.http;

import com.jayway.restassured.response.Response;
import com.mbi.Parameters;
import com.mbi.Request;

public class PostHttpMethod implements HttpMethod {

    @Override
    public Response request(Request request) {
        Parameters params = request.getParameters();
        Response r = getSpecification(params).post(params.getPath());
        checkStatusCode(r, params);

        return r;
    }
}

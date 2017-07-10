package com.mbi.requestmethod;

import com.mbi.OnRequestPerformedListener;
import com.mbi.RequestBuilder;
import io.restassured.response.Response;

public class GetHttpMethod implements HttpRequestMethod {

    private OnRequestPerformedListener requestListener;

    @Override
    public void setRequestListener(OnRequestPerformedListener requestListener) {
        this.requestListener = requestListener;
    }

    @Override
    public Response request(RequestBuilder builder) {
        Response r;
        try {
            r = getSpecification(builder).get(builder.getPath());
            checkStatusCode(r, builder);
        } finally {
            requestListener.onRequestPerformed();
        }

        return r;
    }
}

package com.mbi.requestmethod;

import com.mbi.OnRequestPerformedListener;
import com.mbi.RequestBuilder;
import io.restassured.response.Response;

/**
 *
 */
public class GetHttpMethod implements HttpRequestMethod {

    private OnRequestPerformedListener requestListener;

    @Override
    public void setRequestListener(final OnRequestPerformedListener requestListener) {
        this.requestListener = requestListener;
    }

    @Override
    public Response request(final RequestBuilder builder) {
        final Response r;
        try {
            r = getSpecification(builder).get(builder.getPath());
            checkStatusCode(r, builder);
        } finally {
            requestListener.onRequestPerformed();
        }

        return r;
    }
}

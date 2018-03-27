package com.mbi;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Objects;

/**
 *
 */
final class HttpRequestMethod {

    private OnRequestPerformedListener requestListener;

    private RequestSpecification getSpecification(final RequestBuilder builder) {
        return new Configurator(builder).getSpec();
    }

    private void checkStatusCode(final Response r, final RequestBuilder builder) {
        // No need to check status code if it's not set
        if (Objects.isNull(builder.getStatusCode())) {
            return;
        }

        r.then().assertThat().statusCode(builder.getStatusCode());
    }

    /**
     * Performs request. Compares status code with expected. Finally resets request builder to default.
     *
     * @param builder request builder.
     * @return response.
     * @throws AssertionError on errors. Exception message contains url, response and request as a curl.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    public Response request(final RequestBuilder builder) {
        Response r = null;
        try {
            r = getSpecification(builder).request(builder.getMethod().toString(), builder.getUrl());
            checkStatusCode(r, builder);
        } catch (Throwable throwable) {
            throw new AssertionError(throwable.getMessage()
                    .concat("\n")
                    .concat(String.format("Url: %s%n%n", builder.getUrl()))
                    .concat(String.format("Response: %s%n%n", Objects.isNull(r) ? null : r.asString()))
                    .concat("Request: " + new CurlGenerator(builder, getSpecification(builder)).getCurl()));
        } finally {
            requestListener.onRequestPerformed();
        }

        return r;
    }

    public void setRequestListener(final OnRequestPerformedListener requestListener) {
        this.requestListener = requestListener;
    }
}

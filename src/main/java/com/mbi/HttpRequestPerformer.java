package com.mbi;

import io.restassured.response.Response;

import java.util.Objects;

/**
 *
 */
final class HttpRequestPerformer {

    private final Configurator config;
    private OnRequestPerformedListener requestListener;

    HttpRequestPerformer(final RequestBuilder builder) {
        this.config = new Configurator(builder);
    }

    private void checkStatusCode(final Response r) {
        // No need to check status code if it's not set
        if (Objects.isNull(config.getBuilder().getStatusCode())) {
            return;
        }

        r.then().assertThat().statusCode(config.getBuilder().getStatusCode());
    }

    /**
     * Performs request. Compares status code with expected. Finally resets request builder to default.
     *
     * @return response.
     * @throws AssertionError on errors. Exception message contains url, response and request as a curl.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    public Response request() {
        Response r = null;
        try {
            r = config.getSpec().request(config.getBuilder().getMethod().toString(), config.getBuilder().getUrl());
            checkStatusCode(r);
        } catch (Throwable throwable) {
            throw new AssertionError(throwable.getMessage()
                    .concat(String.format("%nUrl: %s%n%n", config.getBuilder().getUrl()))
                    .concat(String.format("Response: %s%n%n", Objects.isNull(r) ? null : r.asString()))
                    .concat("Request: " + new CurlGenerator(config).getCurl()));
        } finally {
            requestListener.onRequestPerformed();
        }

        return r;
    }

    public void setRequestListener(final OnRequestPerformedListener requestListener) {
        this.requestListener = requestListener;
    }
}

package com.mbi;

import io.restassured.response.Response;

import java.util.Objects;

/**
 * Performs request.
 */
final class HttpRequestPerformer {

    private final Configurator config;
    private OnRequestPerformedListener requestListener;

    HttpRequestPerformer(final RequestBuilder builder) {
        this.config = new Configurator(builder);
    }

    private void checkStatusCode(final Response r) {
        // No need to check status code if it's not set
        if (Objects.isNull(config.getStatusCode())) {
            return;
        }

        r.then().assertThat().statusCode(config.getStatusCode());
    }

    /**
     * Performs request. Compares status code with expected. Finally resets request builder to default.
     *
     * @param pathParams path parameters.
     * @return response.
     * @throws AssertionError on errors. Exception message contains url, response and request as a curl.
     */
    public Response request(final Object... pathParams) {
        Response r = null;
        try {
            r = config.getSpec().request(config.getMethod(), config.getUrl(), pathParams);
            checkStatusCode(r);
        } catch (AssertionError assertionError) {
            throw new AssertionError(assertionError
                    .getMessage()
                    .concat(String.format("%nUrl: %s%n%n", config.getUrl()))
                    .concat(String.format("Response: %s%n%n", Objects.isNull(r) ? null : r.asString()))
                    .concat(String.format("Request: %s%n%n", new CurlGenerator(config).getCurl())),
                    assertionError);
        } finally {
            requestListener.onRequestPerformed();
        }

        return r;
    }

    public void setRequestListener(final OnRequestPerformedListener requestListener) {
        this.requestListener = requestListener;
    }
}

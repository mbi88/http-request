package com.mbi.request;

import com.mbi.config.RequestConfig;
import com.mbi.utils.CurlGenerator;
import com.mbi.utils.MessageComposer;
import io.restassured.response.Response;

import java.util.Objects;

import static org.testng.Assert.assertEquals;

/**
 * Performs request.
 */
final class HttpRequestPerformer {

    private OnRequestPerformedListener requestListener;

    /**
     * Checks equality of actual response status code and expected status code if it was set.
     *
     * @param response response of a request.
     * @param config   request configuration.
     * @throws AssertionError if actual response status code is not equal to expected.
     */
    private void checkStatusCode(final Response response, final RequestConfig config) {
        // No need to check status code if it's not set
        if (Objects.isNull(config.getExpectedStatusCode())) {
            return;
        }

        try {
            assertEquals(response.statusCode(), config.getExpectedStatusCode().intValue());
        } catch (AssertionError assertionError) {
            final String msg = new MessageComposer(assertionError.getMessage(),
                    config.getUrl(),
                    Objects.isNull(response) ? "null" : response.asString(),
                    new CurlGenerator(config).getCurl(),
                    config.getMaxResponseLength())
                    .composeMessage();
            throw new AssertionError(msg, assertionError);
        }
    }

    /**
     * Performs request. Compares status code with expected. Finally resets request builder to default.
     *
     * @param requestConfig request configuration.
     * @return response.
     * @throws AssertionError on errors. Exception message contains url, response and request as a curl.
     */
    public Response request(final RequestConfig requestConfig) {
        final Response response;
        try {
            response = requestConfig
                    .getRequestSpecification()
                    .request(requestConfig.getMethod(), requestConfig.getUrl(), requestConfig.getPathParams());
            checkStatusCode(response, requestConfig);
        } finally {
            requestListener.onRequestPerformed();
        }

        return response;
    }

    /**
     * Listens to request invocation and resets the builder.
     *
     * @param requestListener listener.
     */
    public void setRequestListener(final OnRequestPerformedListener requestListener) {
        this.requestListener = requestListener;
    }
}

package com.mbi.request;

import com.mbi.config.RequestConfig;
import com.mbi.utils.MessageComposer;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.testng.Assert.assertFalse;

/**
 * Executes the actual HTTP request using a configured RequestSpecification.
 * <p>
 * Performs status code validation and optional 'errors' array check.
 * Notifies any attached listeners after execution.
 */
final class HttpRequestPerformer implements Performable {

    private final List<OnRequestPerformedListener> requestListeners = new ArrayList<>();
    private Response response;
    private RequestConfig config;

    /**
     * Executes the request and performs validations.
     *
     * @param requestConfig request configuration.
     * @return Rest-Assured response object.
     * @throws AssertionError if status code doesn't match or errors are present.
     */
    public Response request(final RequestConfig requestConfig) {
        try {
            this.config = requestConfig;

            this.response = requestConfig
                    .getRequestSpecification()
                    .request(requestConfig.getMethod(), requestConfig.getUrl(), requestConfig.getPathParams());

            checkStatusCode(response, requestConfig);
            checkNoErrors(response, requestConfig);
        } finally {
            requestListeners.forEach(OnRequestPerformedListener::onRequestPerformed);
        }

        return response;
    }

    /**
     * Registers a request listener to be called after execution.
     *
     * @param requestListener listener implementation.
     */
    public void addRequestListener(final OnRequestPerformedListener requestListener) {
        this.requestListeners.add(requestListener);
    }

    /**
     * Called after request execution to log request/response using SLF4J.
     */
    @Override
    public void onRequest() {
        final Logger logger = LoggerFactory.getLogger("file-logger");
        logger.info(String.format("Request: %s%nResponse: %s%n",
                config.toString(),
                Objects.isNull(response) ? "null" : response.asString()));
    }

    /**
     * Validates the response status code against expected values from config.
     *
     * @param response actual response.
     * @param config   request configuration.
     */
    private void checkStatusCode(final Response response, final RequestConfig config) {
        // No need to check status code if it's not set
        if (config.getExpectedStatusCodes() == null) {
            return;
        }

        // If the list of expected status codes doesn't contain the actual status code,
        // we explicitly fail the test with a comparison message.
        // Note: we convert both values to strings to improve the readability of the error message.
        if (!config.getExpectedStatusCodes().contains(response.statusCode())) {
            final var assertionError = new AssertionError(String.format("expected %s but found [%d]",
                    config.getExpectedStatusCodes(), response.statusCode()));
            final String msg = new MessageComposer(assertionError, config, response).composeMessage();
            throw new AssertionError(msg, assertionError);
        }
    }

    /**
     * Validates that the response body does not contain an 'errors' array (if enabled).
     *
     * @param response actual response.
     * @param config   request configuration.
     */
    private void checkNoErrors(final Response response, final RequestConfig config) {
        // No need to check errors code if flag not set
        if (config.isCheckNoErrors() == null || !config.isCheckNoErrors()) {
            return;
        }

        // Check if list has errors
        final var errors = response.body().jsonPath().getList("errors");
        final boolean hasErrors = responseHasErrors(errors);

        try {
            assertFalse(hasErrors, "Response has errors!");
        } catch (AssertionError assertionError) {
            final String msg = new MessageComposer(assertionError, config, response).composeMessage();
            throw new AssertionError(msg, assertionError);
        }
    }

    /**
     * Checks whether the given list of errors contains any non-null elements.
     * <p>
     * This is used to detect if the response body has an 'errors' array with actual errors.
     *
     * @param errors list of error objects parsed from JSON response.
     * @return true if any non-null error is present; false otherwise.
     */
    private boolean responseHasErrors(final List<Object> errors) {
        boolean hasErrors = false;

        if (errors != null) {
            for (var error : errors) {
                if (error != null) {
                    hasErrors = true;
                    break;
                }
            }
        }

        return hasErrors;
    }
}

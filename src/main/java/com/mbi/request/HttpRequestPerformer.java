package com.mbi.request;

import com.mbi.config.RequestConfig;
import com.mbi.utils.MessageComposer;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static org.testng.Assert.*;

/**
 * Performs request.
 */
final class HttpRequestPerformer implements Performable {

    private final List<OnRequestPerformedListener> requestListeners = new ArrayList<>();
    private Response response;
    private RequestConfig config;

    /**
     * Checks equality of actual response status code and expected status code if it was set.
     *
     * @param response response of a request.
     * @param config   request configuration.
     * @throws AssertionError if actual response status code is not equal to expected.
     */
    private void checkStatusCode(final Response response, final RequestConfig config) {
        // No need to check status code if it's not set
        if (Objects.isNull(config.getExpectedStatusCodes())) {
            return;
        }

        try {
            if (!config.getExpectedStatusCodes().contains(response.statusCode())) {
                assertEquals(String.valueOf(response.statusCode()), config.getExpectedStatusCodes().toString());
            }
        } catch (AssertionError assertionError) {
            final String msg = new MessageComposer(assertionError, config, response).composeMessage();
            throw new AssertionError(msg, assertionError);
        }
    }

    /**
     * Check if response has 'errors' array.
     *
     * @param response response of a request.
     * @param config   request configuration.
     * @throws AssertionError if actual response has 'errors' array.
     */
    private void checkNoErrors(final Response response, final RequestConfig config) {
        // No need to check errors code if flag not set
        if (Objects.isNull(config.isCheckNoErrors()) || !config.isCheckNoErrors()) {
            return;
        }

        final var errors = response.body().jsonPath().getList("errors");
        // Check if list has errors
        final Predicate<List<Object>> checkListHasErrors = list -> {
            var listHasErrors = false;
            for (var o : list) {
                if (!Objects.isNull(o)) {
                    listHasErrors = true;
                    break;
                }
            }
            return listHasErrors;
        };
        final boolean hasErrors = errors != null && checkListHasErrors.test(errors);

        try {
            assertFalse(hasErrors, "Response has errors!");
        } catch (AssertionError assertionError) {
            final String msg = new MessageComposer(assertionError, config, response).composeMessage();
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
     * Listens to request invocation.
     *
     * @param requestListener listener.
     */
    public void addRequestListener(final OnRequestPerformedListener requestListener) {
        this.requestListeners.add(requestListener);
    }

    @Override
    public void onRequest() {
        final Logger logger = LoggerFactory.getLogger("file-logger");
        logger.info(String.format("Request: %s%nResponse: %s%n",
                config.toString(),
                Objects.isNull(response) ? "null" : response.asString()));
    }
}

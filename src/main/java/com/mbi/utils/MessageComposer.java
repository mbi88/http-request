package com.mbi.utils;

import com.mbi.config.RequestConfig;
import io.restassured.response.Response;

/**
 * Composes detailed assertion error message for failed HTTP requests.
 * <p>
 * Includes:
 * - Original assertion error message.
 * - Request URL.
 * - Response body (optionally truncated).
 * - Curl command for reproducing request.
 */
public final class MessageComposer {

    private final String error;
    private final String url;
    private final String response;
    private final String request;
    private final int responseLength;

    /**
     * Constructs message composer for a failed request.
     *
     * @param error    original assertion error.
     * @param config   request config, used for url, curl, and max response length.
     * @param response actual response from server.
     */
    public MessageComposer(final AssertionError error, final RequestConfig config, final Response response) {
        this.error = error.getMessage();
        this.url = String.format("%nUrl: %s", config.getUrl());
        this.responseLength = config.getMaxResponseLength();
        this.response = String.format("%n%nResponse: %s%n", cutResponse(response.asString()));
        this.request = String.format("%n%nRequest: %s%n%n", new CurlGenerator(config).getCurl());
    }

    /**
     * Builds final formatted error message.
     *
     * @return detailed message with error, URL, response body and curl.
     */
    public String composeMessage() {
        return error
                .concat(url)
                .concat(response)
                .concat(request);
    }

    /**
     * Truncates the response string if a max length is configured.
     *
     * @param response full response string.
     * @return trimmed or full response string.
     */
    private String cutResponse(final String response) {
        return (responseLength > 0)
                ? response.substring(0, Math.min(responseLength, response.length()))
                : response;
    }
}

package com.mbi.utils;


/**
 * Composes error message on request failure.
 */
public final class MessageComposer {

    private final String error;
    private final String url;
    private final String response;
    private final String request;
    private final int responseLength;

    public MessageComposer(
            final String error,
            final String url,
            final String response,
            final String curl,
            final int responseLength) {
        this.error = error;
        this.url = String.format("%nUrl: %s", url);
        this.response = String.format("%n%nResponse: %s%n", response);
        this.request = String.format("%n%nRequest: %s%n%n", curl);
        this.responseLength = responseLength;
    }

    public String composeMessage() {
        return error
                .concat(url)
                .concat(cutResponse(response))
                .concat(request);
    }

    private String cutResponse(final String response) {
        return (responseLength > 0) ? response.substring(0, Math.min(responseLength, response.length())) : response;
    }
}

package com.mbi.utils;

import com.mbi.config.RequestConfig;
import io.restassured.http.Header;

import java.util.List;

/**
 * Generates a curl command representing the HTTP request.
 * Useful for debugging and reproducing API calls in a terminal.
 */
public final class CurlGenerator {

    private final RequestConfig config;

    /**
     * Constructs a CurlGenerator based on the request configuration.
     *
     * @param config request config with headers, URL, method, and body.
     */
    public CurlGenerator(final RequestConfig config) {
        this.config = config;
    }

    /**
     * Builds a curl command string from the current request configuration.
     *
     * @return curl-formatted string.
     */
    public String getCurl() {
        return "curl"
                + getMethod()
                + getUrl()
                + getHeaders()
                + getData();
    }

    /**
     * Appends HTTP method to the curl command (e.g. -X POST).
     *
     * @return formatted method string.
     */
    private String getMethod() {
        return String.format(" -X %s", config.getMethod());
    }

    /**
     * Appends the target URL to the curl command.
     *
     * @return formatted URL string.
     */
    private String getUrl() {
        return String.format(" '%s'", config.getUrl());
    }

    /**
     * Appends all configured headers to the curl command.
     *
     * @return formatted header string(s).
     */
    private String getHeaders() {
        final List<Header> headers = config.getHeaders();
        if (headers == null || headers.isEmpty()) {
            return "";
        }

        final var builder = new StringBuilder();
        for (var header : headers) {
            builder.append(" -H ")
                    .append(String.format("'%s: %s'", header.getName(), header.getValue()));
        }
        return builder.toString();
    }

    /**
     * Appends the body data to the curl command using --data flag.
     *
     * @return formatted body string, or empty string if no body is set.
     */
    private String getData() {
        final var data = config.getData();
        if (data == null) {
            return "";
        }

        return String.format(" --data '%s'", data);
    }
}

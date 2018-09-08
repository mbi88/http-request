package com.mbi;

import java.util.Map;

import static java.lang.String.format;

/**
 * Configuration POJO.
 */
public final class Configuration {

    private Integer connectionTimeout;
    private Map<String, String> headers;

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return format("connectionTimeout: %s%n", connectionTimeout)
                .concat(format("headers: %s%n", headers));
    }
}

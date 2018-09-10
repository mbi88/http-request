package com.mbi;

import java.util.Map;

/**
 * Configuration POJO.
 */
public final class Configuration {

    private Integer connectionTimeout;
    private Map<String, String> headers;
    private Integer maxResponseLength;

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public Integer getMaxResponseLength() {
        return this.maxResponseLength;
    }

    public void setMaxResponseLength(final Integer maxResponseLength) {
        this.maxResponseLength = maxResponseLength;
    }
}

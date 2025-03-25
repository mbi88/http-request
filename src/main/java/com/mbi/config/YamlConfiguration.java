package com.mbi.config;

import java.util.Map;

/**
 * Represents optional configuration loaded from YAML file (`http-request.yml`).
 * Used to set default headers, connection timeouts, and response truncation for logging.
 */
public final class YamlConfiguration {

    /**
     * Optional connection timeout in milliseconds. Applies to both connect and socket timeouts.
     */
    private Integer connectionTimeout;

    /**
     * Optional default headers applied to all requests.
     */
    private Map<String, String> headers;

    /**
     * Optional max response length for error logs. 0 or null = no limit.
     */
    private Integer maxResponseLength;

    /**
     * Gets the configured connection timeout in milliseconds.
     *
     * @return timeout or null.
     */
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Sets the connection timeout value.
     *
     * @param connectionTimeout timeout in ms.
     */
    public void setConnectionTimeout(final Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Gets the default headers defined in the YAML file.
     *
     * @return map of header name → value, or null.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets the default headers.
     *
     * @param headers map of header name → value.
     */
    public void setHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Gets max allowed response body length in characters for error reporting.
     *
     * @return character count limit or null.
     */
    public Integer getMaxResponseLength() {
        return this.maxResponseLength;
    }

    /**
     * Sets max allowed response length for logging.
     *
     * @param maxResponseLength number of characters to keep.
     */
    public void setMaxResponseLength(final Integer maxResponseLength) {
        this.maxResponseLength = maxResponseLength;
    }
}

package com.mbi.request;

/**
 * Interface for components that should react after an HTTP request is performed.
 * <p>
 * Typical use cases:
 * - Logging request and response.
 * - Resetting request builders.
 * - Custom post-processing hooks.
 */
@FunctionalInterface
public interface Performable {

    /**
     * Called automatically after an HTTP request is executed.
     */
    void onRequest();
}

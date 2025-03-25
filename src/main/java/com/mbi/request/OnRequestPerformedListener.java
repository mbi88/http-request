package com.mbi.request;

/**
 * Hook that is invoked after an HTTP request has been executed.
 * <p>
 * Can be used for logging, metrics, or resetting builders.
 */
@FunctionalInterface
public interface OnRequestPerformedListener {

    /**
     * Called after request execution is complete.
     */
    void onRequestPerformed();
}

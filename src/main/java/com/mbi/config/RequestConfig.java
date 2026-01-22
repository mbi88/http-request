package com.mbi.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.restassured.http.Header;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;

import java.util.List;

/**
 * Holds all configuration details for an HTTP request.
 * This class is used to build, track, and debug requests.
 */
public class RequestConfig {

    /**
     * HTTP method to use (GET, POST, etc.).
     */
    private Method method;

    /**
     * Target request URL.
     */
    private String url;

    /**
     * Name of the test method that initiated the request (for logging).
     */
    private String callerTestMethod;

    /**
     * Body payload.
     */
    private Object data;

    /**
     * Rest-Assured request specification (transient: not serialized).
     */
    private transient RequestSpecification requestSpecification;

    /**
     * List of headers to include in the request.
     */
    private List<Header> headers;

    /**
     * List of acceptable response status codes.
     */
    private List<Integer> expectedStatusCodes;

    /**
     * Optional path parameters.
     */
    private Object[] pathParams;

    /**
     * Max characters of response body to include in error logs (0 = unlimited).
     */
    private int maxResponseLength;

    /**
     * Enables full debug logging of request/response.
     */
    private boolean debug;

    /**
     * If true, response must NOT contain 'errors' array.
     */
    private Boolean checkNoErrors;

    public Method getMethod() {
        return method;
    }

    public void setMethod(final Method method) {
        this.method = method;
    }

    public RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    public void setRequestSpecification(final RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(final List<Header> headers) {
        this.headers = headers;
    }

    public List<Integer> getExpectedStatusCodes() {
        return expectedStatusCodes;
    }

    public void setExpectedStatusCodes(final List<Integer> expectedStatusCodes) {
        this.expectedStatusCodes = expectedStatusCodes;
    }

    /**
     * Returns a clone of the path parameters array (to avoid accidental mutation).
     */
    public Object[] getPathParams() {
        return pathParams != null ? pathParams.clone() : new Object[0];
    }

    public void setPathParams(final Object... pathParams) {
        this.pathParams = pathParams;
    }

    public int getMaxResponseLength() {
        return maxResponseLength;
    }

    public void setMaxResponseLength(final int maxResponseLength) {
        this.maxResponseLength = maxResponseLength;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    public Boolean isCheckNoErrors() {
        return checkNoErrors;
    }

    public void setCheckNoErrors(final Boolean checkNoErrors) {
        this.checkNoErrors = checkNoErrors;
    }

    public String getCallerTestMethod() {
        return callerTestMethod;
    }

    public void setCallerTestMethod(final String callerTestMethod) {
        this.callerTestMethod = callerTestMethod;
    }

    /**
     * Converts current configuration to JSON string.
     *
     * @return JSON-formatted string.
     */
    @Override
    public String toString() {
        final var gson = new GsonBuilder()
                .disableHtmlEscaping() // optional: do not escape characters like <, >, &
                .create();

        // First, convert this to a JsonObject ("data" is still a String here)
        // Then, replace the "data" field, if present, with parsed JSON
        final var root = gson.toJsonTree(this).getAsJsonObject();

        if (data != null) {
            try {
                // Try to parse "data" as JSON
                final var parsed = JsonParser.parseString(data.toString());
                // Replace the "data" field with the parsed JSON element
                root.add("data", parsed);
            } catch (JsonSyntaxException ignored) {
                // If parsing fails, keep "data" as a string
            }
        }

        return gson.toJson(root);
    }
}

package com.mbi.request;

import com.mbi.HttpRequest;
import com.mbi.config.RequestDirector;
import io.restassured.http.Header;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Thread-safe builder for HTTP requests.
 * <p>
 * Stores request state using ThreadLocal fields, allowing isolated usage in concurrent tests.
 * After each request is executed, the internal state is automatically cleared via {@link #onRequest()}.
 */
@SuppressWarnings("PMD.LinguisticNaming")
public final class RequestBuilder implements HttpRequest, Performable {

    private final ThreadLocal<String> urlThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Method> methodsThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Object> dataThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<List<Integer>> statusCodesThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<String> tokenThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<List<Header>> headersThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<RequestSpecification> specificationThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Boolean> debugThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Object[]> pathParamsThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Boolean> checkNoErrorsThreadLocal = new ThreadLocal<>();

    /**
     * Sets a single header.
     */
    @Override
    public HttpRequest setHeader(final String header, final String value) {
        final List<Header> list = Objects.isNull(getHeaders()) ? new ArrayList<>() : getHeaders();
        list.add(new Header(header, value));
        headersThreadLocal.set(list);
        return this;
    }

    @Override
    public HttpRequest setHeaders(final List<Header> headers) {
        headersThreadLocal.set(headers);
        return this;
    }

    public List<Header> getHeaders() {
        return headersThreadLocal.get();
    }

    @Override
    public HttpRequest setData(final Object data) {
        dataThreadLocal.set(data);
        return this;
    }

    public Object getData() {
        return dataThreadLocal.get();
    }

    @Override
    public HttpRequest setToken(final String token) {
        tokenThreadLocal.set(token);
        return this;
    }

    public String getToken() {
        return tokenThreadLocal.get();
    }

    @Override
    public HttpRequest setExpectedStatusCode(final Integer statusCode) {
        final List<Integer> list = new ArrayList<>();
        list.add(statusCode);
        statusCodesThreadLocal.set(list);
        return this;
    }

    @Override
    public HttpRequest setExpectedStatusCodes(final List<Integer> codes) {
        statusCodesThreadLocal.set(codes);
        return this;
    }

    @Override
    public HttpRequest checkNoErrors(final Boolean noErrors) {
        checkNoErrorsThreadLocal.set(noErrors);
        return this;
    }

    public Boolean getNoErrors() {
        return checkNoErrorsThreadLocal.get();
    }

    public List<Integer> getStatusCodes() {
        return statusCodesThreadLocal.get();
    }

    @Override
    public HttpRequest setRequestSpecification(final RequestSpecification specification) {
        specificationThreadLocal.set(specification);
        return this;
    }

    public RequestSpecification getSpecification() {
        return specificationThreadLocal.get();
    }

    @Override
    public HttpRequest setUrl(final String url) {
        urlThreadLocal.set(url);
        return this;
    }

    public String getUrl() {
        return urlThreadLocal.get();
    }

    public Boolean getDebug() {
        return Boolean.TRUE.equals(debugThreadLocal.get());
    }

    @Override
    public HttpRequest debug() {
        debugThreadLocal.set(true);
        return this;
    }

    private HttpRequest debug(final boolean isDebug) {
        debugThreadLocal.set(isDebug);
        return this;
    }

    public Method getMethod() {
        return methodsThreadLocal.get();
    }

    private void setMethod(final Method method) {
        methodsThreadLocal.set(method);
    }

    public Object[] getPathParams() {
        return pathParamsThreadLocal.get();
    }

    private void setPathParams(final Object... pathParams) {
        pathParamsThreadLocal.set(pathParams);
    }

    /**
     * Internal request logic shared by all HTTP method implementations.
     */
    private Response doRequest(final String url, final Method method, final Object... pathParams) {
        setUrl(url);
        setMethod(method);
        setPathParams(pathParams);

        final RequestDirector requestDirector = new RequestDirector(this);
        requestDirector.constructRequest();

        final HttpRequestPerformer httpRequest = new HttpRequestPerformer();
        httpRequest.addRequestListener(httpRequest::onRequest);
        httpRequest.addRequestListener(this::onRequest);

        return httpRequest.request(requestDirector.getRequestConfig());
    }

    @Override
    public Response post(final String url, final Object... pathParams) {
        return doRequest(url, Method.POST, pathParams);
    }

    @Override
    public Response get(final String url, final Object... pathParams) {
        return doRequest(url, Method.GET, pathParams);
    }

    @Override
    public Response put(final String url, final Object... pathParams) {
        return doRequest(url, Method.PUT, pathParams);
    }

    @Override
    public Response patch(final String url, final Object... pathParams) {
        return doRequest(url, Method.PATCH, pathParams);
    }

    @Override
    public Response delete(final String url, final Object... pathParams) {
        return doRequest(url, Method.DELETE, pathParams);
    }

    /**
     * Clears all builder state after request execution to ensure reusability in concurrent environments.
     */
    @Override
    public void onRequest() {
        setUrl(null);
        setData(null);
        setExpectedStatusCode(null);
        setExpectedStatusCodes(null);
        setToken(null);
        setHeaders(null);
        setMethod(null);
        setRequestSpecification(null);
        debug(false);
        checkNoErrors(null);
    }
}

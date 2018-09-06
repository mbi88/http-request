package com.mbi;

import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Request builder. All fields are thread local.
 * <p>
 * ThreadLocal variables are not static in this case.
 * Is meant as a single container per instance, not container per class.
 * May create memory leak.
 */
public final class RequestBuilder implements HttpRequest, Resettable {

    private final ThreadLocal<String> urlThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<HttpMethods> methodsThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Object> dataThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Integer> statusCodeThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<String> tokenThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<List<Header>> headersThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<RequestSpecification> specificationThreadLocal = new ThreadLocal<>();

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

    @Override
    public HttpRequest setData(final Object data) {
        dataThreadLocal.set(data);
        return this;
    }

    @Override
    public HttpRequest setToken(final String token) {
        tokenThreadLocal.set(token);
        return this;
    }

    @Override
    public HttpRequest setExpectedStatusCode(final Integer statusCode) {
        statusCodeThreadLocal.set(statusCode);
        return this;
    }

    @Override
    public HttpRequest setRequestSpecification(final RequestSpecification specification) {
        specificationThreadLocal.set(specification);
        return this;
    }

    @Override
    public HttpRequest setUrl(final String url) {
        urlThreadLocal.set(url);
        return this;
    }

    public String getUrl() {
        return urlThreadLocal.get();
    }

    public Object getData() {
        return dataThreadLocal.get();
    }

    public Integer getStatusCode() {
        return statusCodeThreadLocal.get();
    }

    public String getToken() {
        return tokenThreadLocal.get();
    }

    public List<Header> getHeaders() {
        return headersThreadLocal.get();
    }

    public RequestSpecification getSpecification() {
        return specificationThreadLocal.get();
    }

    public HttpMethods getMethod() {
        return methodsThreadLocal.get();
    }

    private void setMethod(final HttpMethods method) {
        methodsThreadLocal.set(method);
    }

    @Override
    public Response post(final String url) {
        setUrl(url);
        setMethod(HttpMethods.POST);

        final HttpRequestPerformer httpMethod = new HttpRequestPerformer(this);
        httpMethod.setRequestListener(this::reset);

        return httpMethod.request();
    }

    @Override
    public Response get(final String url) {
        setUrl(url);
        setMethod(HttpMethods.GET);

        final HttpRequestPerformer httpMethod = new HttpRequestPerformer(this);
        httpMethod.setRequestListener(this::reset);

        return httpMethod.request();
    }

    @Override
    public Response put(final String url) {
        setUrl(url);
        setMethod(HttpMethods.PUT);

        final HttpRequestPerformer httpMethod = new HttpRequestPerformer(this);
        httpMethod.setRequestListener(this::reset);

        return httpMethod.request();
    }

    @Override
    public Response patch(final String url) {
        setUrl(url);
        setMethod(HttpMethods.PATCH);

        final HttpRequestPerformer httpMethod = new HttpRequestPerformer(this);
        httpMethod.setRequestListener(this::reset);

        return httpMethod.request();
    }

    @Override
    public Response delete(final String url) {
        setUrl(url);
        setMethod(HttpMethods.DELETE);

        final HttpRequestPerformer httpMethod = new HttpRequestPerformer(this);
        httpMethod.setRequestListener(this::reset);

        return httpMethod.request();
    }

    /**
     * Resets builder after request invocation.
     */
    @Override
    public void reset() {
        setUrl(null);
        setData(null);
        setExpectedStatusCode(null);
        setToken(null);
        setHeaders(null);
        setMethod(null);
        setRequestSpecification(null);
    }
}

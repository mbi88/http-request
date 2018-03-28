package com.mbi;

import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 */
public final class RequestBuilder implements HttpRequest, Resettable {

    private static final ThreadLocal<String> URL_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<HttpMethods> METHOD_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Object> DATA_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Integer> STATUS_CODE_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> TOKEN_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<List<Header>> HEADERS_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<RequestSpecification> SPECIFICATION_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public HttpRequest setHeader(final String header, final String value) {
        final List<Header> list = Objects.isNull(getHeaders()) ? new ArrayList<>() : getHeaders();
        list.add(new Header(header, value));
        HEADERS_THREAD_LOCAL.set(list);
        return this;
    }

    @Override
    public HttpRequest setHeaders(final List<Header> headers) {
        HEADERS_THREAD_LOCAL.set(headers);
        return this;
    }

    @Override
    public HttpRequest setData(final Object data) {
        DATA_THREAD_LOCAL.set(data);
        return this;
    }

    @Override
    public HttpRequest setToken(final String token) {
        TOKEN_THREAD_LOCAL.set(token);
        return this;
    }

    @Override
    public HttpRequest setExpectedStatusCode(final Integer statusCode) {
        STATUS_CODE_THREAD_LOCAL.set(statusCode);
        return this;
    }

    @Override
    public HttpRequest setRequestSpecification(final RequestSpecification specification) {
        SPECIFICATION_THREAD_LOCAL.set(specification);
        return this;
    }

    public String getUrl() {
        return URL_THREAD_LOCAL.get();
    }

    private void setUrl(final String url) {
        URL_THREAD_LOCAL.set(url);
    }

    public Object getData() {
        return DATA_THREAD_LOCAL.get();
    }

    public Integer getStatusCode() {
        return STATUS_CODE_THREAD_LOCAL.get();
    }

    public String getToken() {
        return TOKEN_THREAD_LOCAL.get();
    }

    public List<Header> getHeaders() {
        return HEADERS_THREAD_LOCAL.get();
    }

    public RequestSpecification getSpecification() {
        return SPECIFICATION_THREAD_LOCAL.get();
    }

    public HttpMethods getMethod() {
        return METHOD_THREAD_LOCAL.get();
    }

    private void setMethod(final HttpMethods method) {
        METHOD_THREAD_LOCAL.set(method);
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

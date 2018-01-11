package com.mbi;

import com.mbi.requestmethod.*;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.List;

public class RequestBuilder implements HttpRequest {

    private static final ThreadLocal<String> PATH_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> METHOD_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Object> DATA_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Integer> STATUS_CODE_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> TOKEN_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<List<Header>> HEADERS_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<RequestSpecification> SPECIFICATION_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public HttpRequest setHeader(String header, String value) {
        List<Header> list = new ArrayList<>();
        list.add(new Header(header, value));
        HEADERS_THREAD_LOCAL.set(list);

        return this;
    }

    @Override
    public HttpRequest setHeader(List<Header> header) {
        HEADERS_THREAD_LOCAL.set(header);

        return this;
    }

    @Override
    public HttpRequest setData(Object data) {
        DATA_THREAD_LOCAL.set(data);

        return this;
    }

    @Override
    public HttpRequest setToken(String token) {
        TOKEN_THREAD_LOCAL.set(token);

        return this;
    }

    @Override
    public HttpRequest setExpectedStatusCode(Integer statusCode) {
        STATUS_CODE_THREAD_LOCAL.set(statusCode);

        return this;
    }

    @Override
    public HttpRequest setPath(String path) {
        PATH_THREAD_LOCAL.set(path);

        return this;
    }

    @Override
    public HttpRequest setMethod(String method) {
        METHOD_THREAD_LOCAL.set(method);

        return this;
    }

    @Override
    public HttpRequest setRequestSpecification(RequestSpecification specification) {
        SPECIFICATION_THREAD_LOCAL.set(specification);

        return this;
    }

    public String getPath() {
        return PATH_THREAD_LOCAL.get();
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

    public String getMethod() {
        return METHOD_THREAD_LOCAL.get();
    }

    @Override
    public Response post(String path) {
        setPath(path);
        PostHttpMethod httpMethod = new PostHttpMethod();
        httpMethod.setRequestListener(this::resetBuilder);

        return httpMethod.request(this);
    }

    @Override
    public Response get(String path) {
        setPath(path);
        GetHttpMethod httpMethod = new GetHttpMethod();
        httpMethod.setRequestListener(this::resetBuilder);

        return httpMethod.request(this);
    }

    @Override
    public Response put(String path) {
        setPath(path);
        PutHttpMethod httpMethod = new PutHttpMethod();
        httpMethod.setRequestListener(this::resetBuilder);

        return httpMethod.request(this);
    }

    @Override
    public Response patch(String path) {
        setPath(path);
        PatchHttpMethod httpMethod = new PatchHttpMethod();
        httpMethod.setRequestListener(this::resetBuilder);

        return httpMethod.request(this);
    }

    @Override
    public Response delete(String path) {
        setPath(path);
        DeleteHttpMethod httpMethod = new DeleteHttpMethod();
        httpMethod.setRequestListener(this::resetBuilder);

        return httpMethod.request(this);
    }

    /*
    Reset builder after request invocation
     */
    private void resetBuilder() {
        setPath(null);
        setData(null);
        setExpectedStatusCode(null);
        setToken(null);
        setHeader(new ArrayList<>());
        setRequestSpecification(null);
    }
}

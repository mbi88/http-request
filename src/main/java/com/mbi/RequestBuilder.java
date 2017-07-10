package com.mbi;

import com.mbi.requestmethod.*;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.List;

public class RequestBuilder implements HttpRequest {

    private String path;
    private Object data;
    private int statusCode;
    private String token;
    private List<Header> headers = new ArrayList<>();
    private RequestSpecification specification;

    @Override
    public HttpRequest setHeader(String header, String value) {
        headers.add(new Header(header, value));
        return this;
    }

    @Override
    public HttpRequest setHeader(List<Header> header) {
        headers = header;
        return this;
    }

    @Override
    public HttpRequest setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public HttpRequest setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public HttpRequest setExpectedStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    @Override
    public HttpRequest setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public HttpRequest setRequestSpecification(RequestSpecification specification) {
        this.specification = specification;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Object getData() {
        return data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getToken() {
        return token;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public RequestSpecification getSpecification() {
        return specification;
    }

    @Override
    public Response post() {
        PostHttpMethod httpMethod = new PostHttpMethod();
        httpMethod.setRequestListener(this::resetBuilder);

        return httpMethod.request(this);
    }

    @Override
    public Response get() {
        GetHttpMethod httpMethod = new GetHttpMethod();
        httpMethod.setRequestListener(this::resetBuilder);

        return httpMethod.request(this);
    }

    @Override
    public Response put() {
        PutHttpMethod httpMethod = new PutHttpMethod();
        httpMethod.setRequestListener(this::resetBuilder);

        return httpMethod.request(this);
    }

    @Override
    public Response patch() {
        PatchHttpMethod httpMethod = new PatchHttpMethod();
        httpMethod.setRequestListener(this::resetBuilder);

        return httpMethod.request(this);
    }

    @Override
    public Response delete() {
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
        setExpectedStatusCode(0);
        setToken(null);
        setHeader(new ArrayList<>());
        setRequestSpecification(null);
    }
}

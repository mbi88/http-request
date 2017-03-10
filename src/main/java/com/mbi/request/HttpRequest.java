package com.mbi.request;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

// TODO: 3/2/17 need refactoring with usage builder pattern
public class HttpRequest implements Request, Configurator {

    @Override
    public Response get(String path, int statusCode, String token) {
        Response r = configureRequest(path, token).get(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response get(String path, String token) {
        return configureRequest(path, token).get(path);
    }

    @Override
    public Response get(String path, int statusCode) {
        Response r = configureRequest(path).get(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response post(String path, T data, int statusCode, String token) {
        Response r = configureRequest(path, data, token).post(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response post(String path, T data, String token) {
        return configureRequest(path, data, token).post(path);
    }

    @Override
    public <T> Response post(String path, T data, int statusCode) {
        Response r = configureRequest(path, data).post(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response post(String path, T data) {
        return configureRequest(path, data).post(path);
    }

    @Override
    public Response post(String path, int statusCode, String token) {
        Response r = configureRequest(path, token).post(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response post(String path, int statusCode) {
        Response r = configureRequest(path).post(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response post(String path, T data, RequestSpecification specification) {
        return configureRequest(path, data, specification).post(path);
    }

    @Override
    public <T> Response put(String path, T data, int statusCode, String token) {
        Response r = configureRequest(path, data, token).put(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response put(String path, T data, String token) {
        return configureRequest(path, data, token).put(path);
    }

    @Override
    public <T> Response put(String path, T data, int statusCode) {
        Response r = configureRequest(path, data).put(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response put(String path, int statusCode, String token) {
        Response r = configureRequest(path, token).put(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response patch(String path, T data, int statusCode, String token) {
        Response r = configureRequest(path, data, token).patch(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response patch(String path, T data, String token) {
        return configureRequest(path, data, token).patch(path);
    }

    @Override
    public <T> Response patch(String path, T data, int statusCode) {
        Response r = configureRequest(path, data).patch(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response patch(String path, int statusCode, String token) {
        Response r = configureRequest(path, token).patch(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response delete(String path, T data, int statusCode, String token) {
        Response r = configureRequest(path, data, token).delete(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response delete(String path, int statusCode, String token) {
        Response r = configureRequest(path, token).delete(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response delete(String path, String token) {
        return configureRequest(path, token).delete(path);
    }

    @Override
    public Response delete(String path, int statusCode) {
        Response r = configureRequest(path).delete(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response delete(String path, T data, int statusCode) {
        Response r = configureRequest(path, data).delete(path);
        checkStatus(r, statusCode);

        return r;
    }
}

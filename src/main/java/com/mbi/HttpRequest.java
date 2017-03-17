package com.mbi;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.mbi.http.*;
import com.mbi.methods.Method;


public class HttpRequest implements Method {

    @Override
    public Response get(String path, int statusCode, String token) {
        Builder builder = new Builder()
                .setMethod(new GetHttpMethod())
                .setPath(path)
                .setToken(token)
                .setStatusCode(statusCode);

        return new Director(builder).construct().request();
    }

    @Override
    public Response get(String path, String token) {
        Builder builder = new Builder()
                .setMethod(new GetHttpMethod())
                .setPath(path)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public Response get(String path, int code) {
        Builder builder = new Builder()
                .setMethod(new GetHttpMethod())
                .setPath(path)
                .setStatusCode(code);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response put(String path, T data, int statusCode, String token) {
        Builder builder = new Builder()
                .setMethod(new PutHttpMethod())
                .setPath(path)
                .setData(data)
                .setStatusCode(statusCode)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response put(String path, T data, String token) {
        Builder builder = new Builder()
                .setMethod(new PutHttpMethod())
                .setPath(path)
                .setData(data)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response put(String path, T data, int statusCode) {
        Builder builder = new Builder()
                .setMethod(new PutHttpMethod())
                .setPath(path)
                .setData(data)
                .setStatusCode(statusCode);

        return new Director(builder).construct().request();
    }

    @Override
    public Response put(String path, int statusCode, String token) {
        Builder builder = new Builder()
                .setMethod(new PutHttpMethod())
                .setPath(path)
                .setStatusCode(statusCode)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response patch(String path, T data, int statusCode, String token) {
        Builder builder = new Builder()
                .setMethod(new PatchHttpMethod())
                .setPath(path)
                .setData(data)
                .setStatusCode(statusCode)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response patch(String path, T data, String token) {
        Builder builder = new Builder()
                .setMethod(new PutHttpMethod())
                .setPath(path)
                .setData(data)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response patch(String path, T data, int statusCode) {
        Builder builder = new Builder()
                .setMethod(new PutHttpMethod())
                .setPath(path)
                .setData(data)
                .setStatusCode(statusCode);

        return new Director(builder).construct().request();
    }

    @Override
    public Response patch(String path, int statusCode, String token) {
        Builder builder = new Builder()
                .setMethod(new PutHttpMethod())
                .setPath(path)
                .setStatusCode(statusCode)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response delete(String path, T data, int statusCode, String token) {
        Builder builder = new Builder()
                .setMethod(new DeleteHttpMethod())
                .setPath(path)
                .setData(data)
                .setStatusCode(statusCode)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public Response delete(String path, int statusCode, String token) {
        Builder builder = new Builder()
                .setMethod(new DeleteHttpMethod())
                .setPath(path)
                .setStatusCode(statusCode)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public Response delete(String path, String token) {
        Builder builder = new Builder()
                .setMethod(new DeleteHttpMethod())
                .setPath(path)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public Response delete(String path, int statusCode) {
        Builder builder = new Builder()
                .setMethod(new DeleteHttpMethod())
                .setPath(path)
                .setStatusCode(statusCode);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response delete(String path, T data, int statusCode) {
        Builder builder = new Builder()
                .setMethod(new DeleteHttpMethod())
                .setPath(path)
                .setData(data)
                .setStatusCode(statusCode);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response post(String path, T data, int statusCode, String token) {
        Builder builder = new Builder()
                .setMethod(new PostHttpMethod())
                .setPath(path)
                .setData(data)
                .setStatusCode(statusCode)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response post(String path, T data, String token) {
        Builder builder = new Builder()
                .setMethod(new PostHttpMethod())
                .setPath(path)
                .setData(data)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response post(String path, T data, int statusCode) {
        Builder builder = new Builder()
                .setMethod(new PostHttpMethod())
                .setPath(path)
                .setData(data)
                .setStatusCode(statusCode);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response post(String path, T data) {
        Builder builder = new Builder()
                .setMethod(new PostHttpMethod())
                .setPath(path)
                .setData(data);

        return new Director(builder).construct().request();
    }

    @Override
    public Response post(String path, int statusCode, String token) {
        Builder builder = new Builder()
                .setMethod(new PostHttpMethod())
                .setPath(path)
                .setStatusCode(statusCode)
                .setToken(token);

        return new Director(builder).construct().request();
    }

    @Override
    public Response post(String path, int statusCode) {
        Builder builder = new Builder()
                .setMethod(new PostHttpMethod())
                .setPath(path)
                .setStatusCode(statusCode);

        return new Director(builder).construct().request();
    }

    @Override
    public <T> Response post(String path, T data, RequestSpecification specification) {
        Builder builder = new Builder()
                .setMethod(new PostHttpMethod())
                .setPath(path)
                .setData(data)
                .setSpecification(specification);

        return new Director(builder).construct().request();
    }
}

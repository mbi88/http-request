package com.mbi.methods;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

interface Post {

    <T> Response post(String path, T data, int statusCode, String token);

    <T> Response post(String path, T data, String token);

    <T> Response post(String path, T data, int statusCode);

    <T> Response post(String path, T data);

    Response post(String path, int statusCode, String token);

    Response post(String path, int statusCode);

    <T> Response post(String path, T data, RequestSpecification specification);
}

package com.mbi.methods;

import io.restassured.response.Response;

interface Patch {

    <T> Response patch(String path, T data, int statusCode, String token);

    <T> Response patch(String path, T data, String token);

    <T> Response patch(String path, T data, int statusCode);

    Response patch(String path, int statusCode, String token);
}

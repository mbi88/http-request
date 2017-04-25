package com.mbi.methods;

import io.restassured.response.Response;

interface Get {

    Response get(String path, int statusCode, String token);

    Response get(String path, String token);

    Response get(String path, int statusCode);
}

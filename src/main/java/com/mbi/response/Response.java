package com.mbi.response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class Response {

    private Object body;
    private Integer statusCode;
    private Map<String, String> headers;

    public Object getBody() {
        return body;
    }

    public void setBody(final Object body) {
        this.body = body;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public JSONObject toJson() {
        return new JSONObject();
    }

    public JSONArray toJsonArray() {
        return new JSONArray();
    }

    public void print() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

package com.mbi.config;

import com.mbi.request.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectConfigTest {

    @Test
    void testCanSetHeaders() {
        var requestDirector = new RequestDirector();
        var config = new RequestConfig();
        config.setHeaders(List.of(new Header("h1", "v1"), new Header("h2", "v2"), new Header("h3", "v3")));

        var builder = new RequestBuilder();
        builder.setConfig(config);

        requestDirector.constructRequest(builder);

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject();
        expected.put("maxResponseLength", 0);
        expected.put("requestTimeOut", 600000);
        expected.put("headers", new JSONArray()
                .put(new JSONObject().put("name", "h1").put("value", "v1"))
                .put(new JSONObject().put("name", "h2").put("value", "v2"))
                .put(new JSONObject().put("name", "h3").put("value", "v3")));

        assertTrue(actual.similar(expected));
    }

    @Test
    void name() {
        var requestDirector = new RequestDirector();
        var config = new RequestConfig();
//        config.setExpectedStatusCode(200);

        var builder = new RequestBuilder();
        builder.setConfig(config);

        requestDirector.constructRequest(builder);

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        System.out.println(actual.toString());
    }
}

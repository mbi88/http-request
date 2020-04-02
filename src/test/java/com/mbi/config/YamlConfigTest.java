package com.mbi.config;

import com.mbi.request.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.error.YAMLException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class YamlConfigTest {

    @Test
    public void testCanSetConfigFromFile() {
        var requestDirector = new RequestDirector("http-request-test.yml");
        requestDirector.constructRequest(new RequestBuilder());

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject()
                .put("maxResponseLength", 100)
                .put("requestTimeOut", 600000)
                .put("headers", new JSONArray()
                        .put(new JSONObject().put("name", "Test").put("value", "header"))
                        .put(new JSONObject().put("name", "Header").put("value", "test header")));

        assertTrue(expected.similar(actual));
    }

    @Test
    public void testDefaultYmlConfig() {
        var requestDirector = new RequestDirector();
        requestDirector.constructRequest(new RequestBuilder());

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject()
                .put("maxResponseLength", 0)
                .put("requestTimeOut", 600000)
                .put("headers", new JSONArray()
                        .put(new JSONObject().put("name", "Accept").put("value", "application/json"))
                        .put(new JSONObject().put("name", "Content-Type").put("value", "application/json; charset=UTF-8")));

        assertTrue(expected.similar(actual));
    }

    @Test
    public void testCanSetConfigFromFileWithoutConnectionTimeout() {
        var requestDirector = new RequestDirector("no_timeout.yml");
        requestDirector.constructRequest(new RequestBuilder());

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject()
                .put("maxResponseLength", 100)
                .put("headers", new JSONArray()
                        .put(new JSONObject().put("name", "Test").put("value", "header"))
                        .put(new JSONObject().put("name", "Header").put("value", "test header")));

        assertTrue(expected.similar(actual));
    }

    @Test
    public void testCanSetConfigFromFileWithoutResponseLength() {
        var requestDirector = new RequestDirector("no_response_length.yml");
        requestDirector.constructRequest(new RequestBuilder());

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject()
                .put("requestTimeOut", 10000)
                .put("maxResponseLength", 0)
                .put("headers", new JSONArray()
                        .put(new JSONObject().put("name", "Test").put("value", "header"))
                        .put(new JSONObject().put("name", "Header").put("value", "test header")));

        assertTrue(expected.similar(actual));
    }

    @Test
    public void testCanSetConfigFromFileWithoutHeaders() {
        var requestDirector = new RequestDirector("no_headers.yml");
        requestDirector.constructRequest(new RequestBuilder());

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject()
                .put("maxResponseLength", 100)
                .put("requestTimeOut", 600000)
                .put("headers", new JSONArray());

        assertTrue(expected.similar(actual));
    }

    @Test
    public void testGetNullPointerExceptionIfFileIsEmpty() {
        var requestDirector = new RequestDirector("empty.yml");

        boolean failed = true;
        try {
            requestDirector.constructRequest(new RequestBuilder());
            failed = false;
        } catch (NullPointerException n) {
            assertTrue(failed);
        }
    }

    @Test
    public void testCantLoadFileWithUnexpectedFields() {
        var requestDirector = new RequestDirector("invalid.yml");

        try {
            requestDirector.constructRequest(new RequestBuilder());
        } catch (YAMLException y) {
            assertEquals(y.getCause().getMessage(),
                    "Unable to find property 'a' on class: com.mbi.config.YamlConfiguration");
        }
    }
}

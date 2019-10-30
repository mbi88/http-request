package com.mbi.config;

import com.mbi.request.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.YAMLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class YamlConfigTest {

    @Test
    void testCanSetConfigFromFile() {
        var requestDirector = new RequestDirector();

        var data = requestDirector.getDataFromYamlFile("http-request-test.yml");
        requestDirector.setYamlData(data);
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
    void testDefaultYmlConfig() {
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
    void testCanSetConfigFromFileWithoutConnectionTimeout() {
        var requestDirector = new RequestDirector();

        var data = requestDirector.getDataFromYamlFile("no_timeout.yml");
        requestDirector.setYamlData(data);
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
    void testCanSetConfigFromFileWithoutResponseLength() {
        var requestDirector = new RequestDirector();

        var data = requestDirector.getDataFromYamlFile("no_response_length.yml");
        requestDirector.setYamlData(data);
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
    void testCanSetConfigFromFileWithoutHeaders() {
        var requestDirector = new RequestDirector();

        var data = requestDirector.getDataFromYamlFile("no_headers.yml");
        requestDirector.setYamlData(data);
        requestDirector.constructRequest(new RequestBuilder());

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject()
                .put("maxResponseLength", 100)
                .put("requestTimeOut", 600000)
                .put("headers", new JSONArray());

        assertTrue(expected.similar(actual));
    }

    @Test
    void testGetNullPointerExceptionIfFileIsEmpty() {
        var requestDirector = new RequestDirector();

        var data = requestDirector.getDataFromYamlFile("empty.yml");
        requestDirector.setYamlData(data);

        boolean failed = true;
        try {
            requestDirector.constructRequest(new RequestBuilder());
            failed = false;
        } catch (NullPointerException n) {
            assertTrue(failed);
        }
    }

    @Test
    void testCantLoadFileWithUnexpectedFields() {
        var requestDirector = new RequestDirector();

        var data = requestDirector.getDataFromYamlFile("invalid.yml");
        requestDirector.setYamlData(data);

        try {
            requestDirector.constructRequest(new RequestBuilder());
        } catch (YAMLException y) {
            assertEquals(y.getCause().getMessage(),
                    "Unable to find property 'a' on class: com.mbi.config.YamlConfiguration");
        }
    }
}

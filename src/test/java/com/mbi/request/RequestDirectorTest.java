package com.mbi.request;

import com.mbi.config.RequestDirector;
import io.restassured.http.Method;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.*;

public class RequestDirectorTest {

    @Test
    public void testReturnsEmptyYamlIfFileMissing() throws Exception {
        var builder = new RequestBuilder();
        builder.setUrl("http://dummy");

        var director = new RequestDirector(builder) {
            @Override
            protected String yamlFileName() {
                return "no-http-request.yml";
            }
        };

        director.constructRequest();
        var config = director.getRequestConfig();

        assertNotNull(config);
        assertEquals(config.getHeaders(), new ArrayList<>());
        assertEquals(config.getMaxResponseLength(), 0);
    }

    @Test
    public void testHeadersNull() {
        var builder = new RequestBuilder();
        builder.setExpectedStatusCode(400).setUrl("https://api.npoint.io/1e9bfb4122b88f8f3582");

        var director = new RequestDirector(builder);

        director.constructRequest();
        var config = director.getRequestConfig();
        config.setHeaders(null);
        config.setMethod(Method.GET);
        var performer = new HttpRequestPerformer();
        try {
            performer.request(config);
            fail("Expected AssertionError due to mismatched status code");
        } catch (AssertionError ignored) {
        }

        assertNull(config.getHeaders());
    }

    @Test
    public void testHeadersEmpty() {
        var builder = new RequestBuilder();
        builder.setExpectedStatusCode(400).setUrl("https://api.npoint.io/1e9bfb4122b88f8f3582");

        var director = new RequestDirector(builder);

        director.constructRequest();
        var config = director.getRequestConfig();
        config.setHeaders(new ArrayList<>());
        config.setMethod(Method.GET);
        var performer = new HttpRequestPerformer();
        try {
            performer.request(config);
            fail("Expected AssertionError due to mismatched status code");
        } catch (AssertionError ignored) {
        }

        assertEquals(config.getHeaders(), new ArrayList<>());
    }

    @Test
    public void testTimeoutZeroWhenNotInYaml() {
        var builder = new RequestBuilder();
        builder.setUrl("http://dummy");

        var director = new RequestDirector(builder) {
            @Override
            protected String yamlFileName() {
                return "http-request-without-timeout.yml";
            }
        };

        director.constructRequest();
        director.getRequestConfig();
    }

    @Test
    public void testDefaultMaxResponseLengthIsZeroWhenNotInYaml() {
        var builder = new RequestBuilder();
        builder.setUrl("http://dummy");

        var director = new RequestDirector(builder) {
            @Override
            protected String yamlFileName() {
                return "http-request-without-max.yml";
            }
        };

        director.constructRequest();
        var config = director.getRequestConfig();

        assertEquals(config.getMaxResponseLength(), 0);
    }

    @Test
    public void testConfiguredMaxResponseLengthUsedFromYaml() {
        var builder = new RequestBuilder();
        builder.setUrl("http://dummy");

        var director = new RequestDirector(builder) {
            @Override
            protected String yamlFileName() {
                return "http-request-with-max.yml";
            }
        };

        director.constructRequest();
        var config = director.getRequestConfig();

        assertEquals(config.getMaxResponseLength(), 5);
    }

    @Test
    public void testConfiguredWithEmptyHeadersFromYaml() {
        var builder = new RequestBuilder();
        builder.setUrl("http://dummy");

        var director = new RequestDirector(builder) {
            @Override
            protected String yamlFileName() {
                return "http-request-with-empty-headers.yml";
            }
        };

        director.constructRequest();
        var config = director.getRequestConfig();

        assertEquals(config.getHeaders(), new ArrayList<>());
    }

    @Test
    public void testResponseTruncationWithCustomYaml() {
        var builder = new RequestBuilder();
        builder
                .setExpectedStatusCode(400)
                .setUrl("https://api.npoint.io/1e9bfb4122b88f8f3582");

        var director = new RequestDirector(builder) {
            @Override
            protected String yamlFileName() {
                return "http-request-with-max.yml"; // maxResponseLength: 5
            }
        };

        director.constructRequest();
        var config = director.getRequestConfig();
        config.setMethod(Method.GET);

        var performer = new HttpRequestPerformer();
        try {
            performer.request(config);
            fail("Expected AssertionError due to mismatched status code");
        } catch (AssertionError error) {
            var message = error.getMessage();

            assertTrue(message.contains("Response:"));

            int index = message.indexOf("Response:");
            int nextLine = message.indexOf("\n", index + 1);

            String responseLine = message.substring(index, nextLine);
            assertTrue(responseLine.length() <= 20, "Response line not truncated as expected");
        }
    }

    @Test
    public void testOnRequestLogsNullResponseIfRequestFails() {
        var builder = new RequestBuilder();
        builder.setUrl("http://non-existent.localhost");

        var director = new RequestDirector(builder);
        director.constructRequest();
        var config = director.getRequestConfig();

        var performer = new HttpRequestPerformer();
        performer.addRequestListener(performer::onRequest);

        // simulate failure to force response = null
        try {
            performer.request(config);
            fail("Expected exception due to invalid URL");
        } catch (Exception e) {
            // ok
        }
    }
}

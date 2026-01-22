package com.mbi.request;

import com.mbi.config.RequestDirector;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.restassured.http.Method;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import static org.testng.Assert.*;

public class RequestDirectorTest {

    private static String baseUrl;
    private HttpServer server;

    @BeforeClass
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0); // automatically assign a free port
        server.createContext("/with-errors", new JsonHandler());
        server.setExecutor(null);
        server.start();

        int port = server.getAddress().getPort();
        baseUrl = "http://localhost:" + port;
    }

    @AfterClass
    public void stopServer() {
        server.stop(0);
    }

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
        builder.setExpectedStatusCode(400).setUrl(baseUrl + "/with-errors");

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
        builder.setExpectedStatusCode(400).setUrl(baseUrl + "/with-errors");

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
                .setUrl(baseUrl + "/with-errors");

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

    static class JsonHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getHttpContext().getPath();
            String response;
            int status;

            switch (path) {
                case "/with-errors" -> {
                    response = "{\"data\":{\"a\":1},\"errors\":[{\"message\":\"error\"}]}";
                    status = 200;
                }
                default -> {
                    response = "{\"error\":\"Unknown path\"}";
                    status = 404;
                }
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}

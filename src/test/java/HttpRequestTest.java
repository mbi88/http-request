import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mbi.HttpRequest;
import com.mbi.config.RequestDirector;
import com.mbi.request.RequestBuilder;
import com.mbi.utils.CallerResolver;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.restassured.http.Header;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

public class HttpRequestTest {

    private static String baseUrl;
    private final HttpRequest http = new RequestBuilder();
    private HttpServer server;

    @BeforeClass
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0); // automatically assign a free port
        server.createContext("/success", new JsonHandler());
        server.createContext("/with-errors", new JsonHandler());
        server.createContext("/errors-null", new JsonHandler());
        server.createContext("/errors-in-array", new JsonHandler());
        server.createContext("/one-of-errors-null", new JsonHandler());
        server.createContext("/empty-errors", new JsonHandler());
        server.createContext("/graphql", new JsonHandler());
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
    public void testSetToken() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setToken("token")
                .setExpectedStatusCode(4534)
                .get(baseUrl + "/success"));
        assertTrue(ex.getMessage().contains("--header 'Authorization: token"));
    }

    @Test
    public void testTokenWithSpec() {
        var spec = given().header(new Header("Authorization", "token1"));

        var ex = expectThrows(AssertionError.class, () -> http
                .setRequestSpecification(spec)
                .setExpectedStatusCode(24)
                .setToken("token2")
                .get(baseUrl + "/success"));
        assertTrue(ex.getMessage().contains("--header 'Authorization: token2"));
    }

    @Test
    public void testWithoutToken() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setExpectedStatusCode(356)
                .get(baseUrl + "/success"));
        assertFalse(ex.getMessage().contains("-H 'Authorization:"));
    }

    @Test
    public void testWithTokenInSpec() {
        var spec = given().header(new Header("Authorization", "token1"));

        var ex = expectThrows(AssertionError.class, () -> http
                .setExpectedStatusCode(463)
                .setRequestSpecification(spec)
                .get(baseUrl + "/success"));
        assertTrue(ex.getMessage().contains("--header 'Authorization: token1"));
    }

    @Test
    public void testBodyOverridesSpec() {
        var spec = given().body(1);

        var ex = expectThrows(AssertionError.class, () -> http
                .setRequestSpecification(spec)
                .setExpectedStatusCode(234)
                .setData(2)
                .get(baseUrl + "/success"));

        assertTrue(ex.getMessage().endsWith("""
                --data '
                    2'
                
                """));
    }

    @Test
    public void testDataOverriding() {
        var specification = given()
                .header("spec_header", "spec_header_value")
                .header("h1", "h1_spec")
                .body("body");

        var ex = expectThrows(AssertionError.class, () -> http
                .setRequestSpecification(specification)
                .setData(100)
                .setExpectedStatusCode(100)
                .setHeader("h1", "h1_value")
                .setHeader("h2", "h2_value")
                .setToken("token")
                .setHeader("Cookie", "cookie")
                .get(baseUrl + "/success"));

        assertTrue(ex.getMessage().contains("--data '\n    100'"), "Incorrect --data");
        assertTrue(ex.getMessage().contains("--header 'spec_header: spec_header_value' \\\n  --header 'h1: h1_spec' \\\n  --header 'Authorization: token' \\\n  --header 'h1: h1_value' \\\n  --header 'h2: h2_value' \\\n  --header 'Cookie: cookie'"), "Incorrect headers");
    }

    @Test
    public void testCheckErrorsIfErrorsExist() {
        var ex = expectThrows(AssertionError.class, () -> http
                .checkNoErrors(true)
                .get(baseUrl + "/with-errors"));
        assertTrue(ex.getMessage().startsWith("Response has errors!"));
    }

    @Test
    public void testCheckErrorsIfErrorsExistAndFlagFalse() {
        http
                .checkNoErrors(false)
                .get(baseUrl + "/with-errors");
    }

    @Test
    public void testCheckErrorsIfEmptyErrors() {
        http
                .checkNoErrors(true)
                .get(baseUrl + "/empty-errors");
    }

    @Test
    public void testCheckErrorsIfOneErrorNull() {
        var ex = expectThrows(AssertionError.class, () -> http
                .checkNoErrors(true)
                .get(baseUrl + "/one-of-errors-null"));
        assertTrue(ex.getMessage().startsWith("Response has errors!"));
    }

    @Test
    public void testCheckErrorsIfErrorsNull() {
        http
                .checkNoErrors(true)
                .get(baseUrl + "/errors-null");
    }

    @Test
    public void testCheckErrorsIfHasErrorsInArrayResponse() {
        var ex = expectThrows(AssertionError.class, () -> http
                .checkNoErrors(true)
                .get(baseUrl + "/errors-in-array"));
        assertTrue(ex.getMessage().startsWith("Response has errors!"));
    }

    @Test
    public void testCodeFail() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setExpectedStatusCode(403)
                .get(baseUrl + "/success"));
        assertTrue(ex.getMessage().contains("expected [403] but found [200]"));
    }

    @Test
    public void testStatusCodeIsAcceptedWhenInList() {
        http
                .setExpectedStatusCodes(List.of(200, 404))
                .get(baseUrl + "/success");
    }

    @Test
    public void testAssertionThrownIfStatusCodeNotInList() {
        var builder = new RequestBuilder()
                .setUrl(baseUrl + "/success")
                .setExpectedStatusCodes(List.of(404, 500));
        var ex = expectThrows(AssertionError.class, () -> builder.get(baseUrl + "/success"));
        assertTrue(ex.getMessage().contains("expected [404, 500] but found [200]"));
    }

    @Test
    public void testCodesFail() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setExpectedStatusCodes(List.of(404, 403, 405))
                .get(baseUrl + "/success"));
        assertTrue(ex.getMessage().contains("expected [404, 403, 405] but found [200]"));
    }

    @Test
    public void testSetBody() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setExpectedStatusCode(342)
                .setData(1)
                .get(baseUrl + "/success"));

        assertTrue(ex.getMessage().endsWith("""
                --data '
                    1'
                
                """));
    }

    @Test
    public void testSpecHeaders() {
        var spec = given().header("h1", "v");

        var ex = expectThrows(AssertionError.class, () -> http
                .setToken("wer")
                .setRequestSpecification(spec)
                .setExpectedStatusCode(342)
                .get(baseUrl + "/success"));
        assertTrue(ex.getMessage().contains("--header 'h1: v' \\\n  --header 'Authorization: wer'"));
    }

    @Test
    public void testSetHeaders() {
        var headers = new ArrayList<Header>();
        headers.add(new Header("h1", "v"));
        headers.add(new Header("h2", "v"));

        var ex = expectThrows(AssertionError.class, () -> http
                .setHeaders(headers)
                .setExpectedStatusCode(300)
                .get(baseUrl + "/success"));
        assertTrue(ex.getMessage().contains("--header 'h1: v' \\\n  --header 'h2: v'"));
    }

    @Test
    public void testSetHeader() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setHeader("header1", "v")
                .setHeader("header2", "v")
                .setExpectedStatusCode(300)
                .get(baseUrl + "/success"));
        assertTrue(ex.getMessage().contains("--header 'header1: v' \\\n  --header 'header2: v'"));
    }

    @Test
    public void testReadsYamlWhenFileExists() throws Exception {
        var builder = new RequestBuilder();
        builder.setUrl("http://dummy");

        var director = new RequestDirector(builder);

        director.constructRequest();
        var config = director.getRequestConfig();

        assertEquals(config.getHeaders().get(0), new Header("Accept", "application/json"));
        assertEquals(config.getHeaders().get(1), new Header("Content-Type", "application/json; charset=UTF-8"));
        assertEquals(config.getMaxResponseLength(), Integer.valueOf(0));
    }

    @Test
    public void testDebugEnablesRequestLogging() {
        var builder = new RequestBuilder();
        builder.debug().setUrl("http://dummy");

        var director = new RequestDirector(builder);
        director.constructRequest();

        assertTrue(builder.isDebug());
        assertTrue(director.getRequestConfig().isDebug());
    }

    @Test
    public void testPost() {
        http
                .post(baseUrl + "/errors-null");
    }

    @Test
    public void testDelete() {
        http
                .delete(baseUrl + "/errors-null");
    }

    @Test
    public void testPatch() {
        http
                .patch(baseUrl + "/errors-null");
    }

    @Test
    public void testPut() {
        http
                .put(baseUrl + "/errors-null");
    }

    @Test
    public void testCanPerformGraphQLRequest() {
        var r = http
                .setData("""
                        {"variables":{"accountId":123},"query":"query($accountId: Int!, $agencyAccountId: Int) {    channels(accountId: $accountId, agencyAccountId: $agencyAccountId) {        nodes {            accountId            id            name            updatedAt        }    }}"}""")
                .setExpectedStatusCode(200)
                .setToken("Bearer token")
                .checkNoErrors(true)
                .post(baseUrl + "/graphql");

        assertEquals(r.asString(), "{\"data\":{\"graphql\":true},\"errors\":[]}");
    }

    @Test
    public void testCurlIfDataParserThrowsException() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setData("{{ invalid json }}")
                .setExpectedStatusCode(2020)
                .setToken("Bearer token")
                .checkNoErrors(true)
                .post(baseUrl + "/graphql"));

        assertTrue(ex.getMessage().contains("""
                --data '
                    {{ invalid json }}'"""
        ));
    }

    @Test
    public void testCanRequestWithJsonObjectPayload() {
        var obj = new JsonObject();
        obj.addProperty("a", 1);

        var ex = expectThrows(AssertionError.class, () -> http
                .setData(obj)
                .setExpectedStatusCode(2002)
                .setToken("Bearer token")
                .checkNoErrors(true)
                .post(baseUrl + "/graphql"));

        assertTrue(ex.getMessage().contains("""
                --data '
                    {
                      "a": 1
                    }'"""
        ));
    }

    @Test
    public void testCanRequestWithJsonArrayPayload() {
        var obj1 = new JsonObject();
        obj1.addProperty("a", 1);
        var obj2 = new JsonObject();
        obj2.addProperty("a", 2);
        var array = new JsonArray();
        array.add(obj1);
        array.add(obj2);

        var ex = expectThrows(AssertionError.class, () -> http
                .setData(array)
                .setExpectedStatusCode(2002)
                .setToken("Bearer token")
                .checkNoErrors(true)
                .post(baseUrl + "/graphql"));

        assertTrue(ex.getMessage().contains("""
                --data '
                    [
                      {
                        "a": 1
                      },
                      {
                        "a": 2
                      }
                    ]'"""
        ));
    }

    @Test
    public void testCanGetCallerMethodName() {
        var builder = new RequestBuilder();
        builder.setUrl(baseUrl + "/success");

        var director = new RequestDirector(builder);
        director.constructRequest();
        var config = director.getRequestConfig();
        config.setCallerTestMethod(CallerResolver.getTestEntryPoint());

        assertEquals(config.getCallerTestMethod(), "HttpRequestTest.testCanGetCallerMethodName");
    }

    @Test
    public void testFailedGetRequestHasCurlWithoutSlashInTheEnd() {
        var spec = given().header(new Header("Authorization", "token1"));

        var ex = expectThrows(AssertionError.class, () -> http
                .setRequestSpecification(spec)
                .setExpectedStatusCode(24)
                .setToken("token2")
                .get(baseUrl + "/success"));

        assertTrue(ex.getMessage().endsWith("--header 'Authorization: token2'\n\n"));
    }

    @Test
    public void testFailedPostRequestHasCurlWithDataInTheEnd() {
        var spec = given().header(new Header("Authorization", "token1"));

        var ex = expectThrows(AssertionError.class, () -> http
                .setRequestSpecification(spec)
                .setData("""
                        {"variables":{"accountId":123},"query":"query($accountId: Int!, $agencyAccountId: Int) {    channels(accountId: $accountId, agencyAccountId: $agencyAccountId) {        nodes {            accountId            id            name            updatedAt        }    }}"}""")
                .setExpectedStatusCode(24)
                .setToken("token2")
                .post(baseUrl + "/success"));

        assertTrue(ex.getMessage().endsWith("""
                --data '
                    {
                      "variables": {
                        "accountId": 123
                      },
                      "query": "query($accountId: Int!, $agencyAccountId: Int) {    channels(accountId: $accountId, agencyAccountId: $agencyAccountId) {        nodes {            accountId            id            name            updatedAt        }    }}"
                    }'
                
                """));
    }

    static class JsonHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getHttpContext().getPath();
            String response;
            int status;

            switch (path) {
                case "/success" -> {
                    response = "{\"a\":1}";
                    status = 200;
                }
                case "/with-errors" -> {
                    response = "{\"data\":{\"a\":1},\"errors\":[{\"message\":\"error\"}]}";
                    status = 200;
                }
                case "/errors-null" -> {
                    response = "{\"errors\":null,\"next_steps\":true}";
                    status = 200;
                }
                case "/errors-in-array" -> {
                    response = "[{\"data\":{\"a\":1},\"errors\":[{\"message\":\"error\"}]},{\"data\":{\"a\":1}}]";
                    status = 200;
                }
                case "/one-of-errors-null" -> {
                    response = "{\"errors\":[null,\"Sign up to make bins only you can edit\"]}";
                    status = 200;
                }
                case "/empty-errors" -> {
                    response = "{\"data\":{\"a\":1},\"errors\":[]}";
                    status = 200;
                }
                case "/graphql" -> {
                    response = "{\"data\":{\"graphql\":true},\"errors\":[]}";
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

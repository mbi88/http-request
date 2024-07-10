import com.mbi.HttpRequest;
import com.mbi.request.RequestBuilder;
import io.restassured.http.Header;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

public class HttpRequestTest {

    private final HttpRequest http = new RequestBuilder();

    @Test
    public void testSetHeader() {
        try {
            http
                    .setHeader("header1", "v")
                    .setHeader("header2", "v")
                    .setExpectedStatusCode(300)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'header1: v' -H 'header2: v'"));
        }
    }

    @Test
    public void testSetHeaders() {
        var headers = new ArrayList<Header>();
        headers.add(new Header("h1", "v"));
        headers.add(new Header("h2", "v"));

        try {
            http
                    .setHeaders(headers)
                    .setExpectedStatusCode(300)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'h1: v' -H 'h2: v'"));
        }
    }

    @Test
    public void testWithoutHeaders() {
        try {
            http
                    .setExpectedStatusCode(230)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("curl -X GET 'https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a'"));
        }
    }

    @Test
    public void testHeadersWithSpec() {
        var spec = given().header("h1", "v");
        try {
            http
                    .setRequestSpecification(spec)
                    .setExpectedStatusCode(342)
                    .setHeader("h2", "v")
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'h1: v' -H 'h2: v'"));
        }
    }

    @Test
    public void testSpecHeaders() {
        var spec = given().header("h1", "v");
        try {
            http
                    .setToken("wer")
                    .setRequestSpecification(spec)
                    .setExpectedStatusCode(342)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'h1: v' -H 'Authorization: wer'"));
        }
    }

    @Test
    public void testHeadersOverride() {
        var spec = given().header("Accept", "1");
        try {
            http
                    .setRequestSpecification(spec)
                    .setHeader("Accept", "2")
                    .setExpectedStatusCode(342)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("curl -X GET 'https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a' -H 'Accept: 2'"));
        }
    }

    @Test
    public void testSetBody() {
        try {
            http
                    .setExpectedStatusCode(342)
                    .setData(1).get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("--data '1'\n\n"));
        }
    }

    @Test
    public void testWithoutBody() {
        try {
            http
                    .setExpectedStatusCode(324)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertFalse(error.getMessage().contains(" --data '"));
        }
    }

    @Test
    public void testBodyOverridesSpec() {
        var spec = given().body(1);
        try {
            http
                    .setRequestSpecification(spec)
                    .setExpectedStatusCode(234)
                    .setData(2)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("--data '2'\n\n"));
        }
    }

    @Test
    public void testSetToken() {
        try {
            http
                    .setToken("token")
                    .setExpectedStatusCode(4534)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'Authorization: token"));
        }
    }

    @Test
    public void testTokenWithSpec() {
        var spec = given().header(new Header("Authorization", "token1"));
        try {
            http
                    .setRequestSpecification(spec)
                    .setExpectedStatusCode(24)
                    .setToken("token2")
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'Authorization: token2"));
        }
    }

    @Test
    public void testWithoutToken() {
        try {
            http
                    .setExpectedStatusCode(356)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertFalse(error.getMessage().contains("-H 'Authorization:"));
        }
    }

    @Test
    public void testWithTokenInSpec() {
        var spec = given().header(new Header("Authorization", "token1"));
        try {
            http
                    .setExpectedStatusCode(463)
                    .setRequestSpecification(spec)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'Authorization: token1"));
        }
    }

    @Test
    public void testCodeFail() {
        boolean passed;
        try {
            http
                    .setExpectedStatusCode(403)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
            passed = true;
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("expected [[403]] but found [200]"));
            passed = false;
        }

        assertFalse(passed);
    }

    @Test
    public void testCodesFail() {
        boolean passed;
        try {
            http
                    .setExpectedStatusCodes(List.of(404, 403, 405))
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
            passed = true;
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("expected [[404, 403, 405]] but found [200]"));
            passed = false;
        }

        assertFalse(passed);
    }

    @Test
    public void testCodesSuccess() {
        http
                .setExpectedStatusCodes(List.of(200, 404, 500))
                .get("https://run.mocky.io/v3/c7de2fe2-0458-4eea-a7ef-dadd94331b94");
    }

    @Test
    public void testCodeSuccess() {
        http
                .setExpectedStatusCode(404)
                .get("https://run.mocky.io/v3/c7de2fe2-0458-4eea-a7ef-dadd94331b94");
    }

    @Test
    public void testWithoutCode() {
        var r = http.get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");

        assertEquals(r.asString(), """
                {"a":1}""");
    }

    @Test
    public void testSpec() {
        var spec = given()
                .header(new Header("Authorization", "token1"))
                .body(1);

        try {
            http
                    .setRequestSpecification(spec)
                    .setExpectedStatusCode(234)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("curl -X GET 'https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a' -H 'Authorization: token1' --data '1'"));
        }
    }

    @Test
    public void testWithoutSpec() {
        try {
            http
                    .setExpectedStatusCode(342)
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains(" curl -X GET 'https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a'"));
        }
    }

    @Test
    public void testGet() {
        http.setExpectedStatusCode(200).get("https://dummyjson.com/products/1");
    }

    @Test
    public void testPost() {
        http
                .setData("data")
                .setExpectedStatusCode(201)
                .post("https://dummyjson.com/products/add");
    }

    @Test
    public void testDelete() {
        http.setExpectedStatusCode(200).delete("https://dummyjson.com/products/1");
    }

    @Test
    public void testPatch() {
        http.setExpectedStatusCode(200).patch("https://dummyjson.com/products/1");
    }

    @Test
    public void testPut() {
        http.setExpectedStatusCode(200).put("https://dummyjson.com/products/1");
    }

    @Test
    public void testBuildersResetAfterRequest() {
        try {
            http.setExpectedStatusCode(234).setData(1).get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("--data '1'\n\n"));
        }

        try {
            http.setExpectedStatusCode(4353).get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError error) {
            assertFalse(error.getMessage().endsWith("--data '1'\n\n"));
        }
    }

    @Test
    public void testThreadLocalIsSingleContainerPerInstanceNotPerClass() {
        var httpRequest1 = new RequestBuilder();
        var httpRequest2 = new RequestBuilder();

        httpRequest1
                .setData("123")
                .setToken(httpRequest2.get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a").asString());

        assertEquals(httpRequest1.getData().toString(), "123");
        assertNull(httpRequest2.getData());
    }

    @Test
    public void testDataOverriding() {
        var specification = given()
                .header("spec_header", "spec_header_value")
                .header("h1", "h1_spec")
                .body("body");

        try {
            http
                    .setRequestSpecification(specification)
                    .setData(100)
                    .setExpectedStatusCode(100)
                    .setHeader("h1", "h1_value")
                    .setHeader("h2", "h2_value")
                    .setToken("token")
                    .setHeader("Cookie", "cookie")
                    .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("--data '100'"), "Incorrect --data");
            assertTrue(e.getMessage().contains("-H 'spec_header: spec_header_value' -H 'h1: h1_spec' -H 'Authorization:"
                            + " token' -H 'h1: h1_value' -H 'h2: h2_value' -H 'Cookie: cookie'"),
                    "Incorrect headers");
        }
    }

    @Test
    public void testExceptionOnInvalidUrl() {
        try {
            http.get("asd");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Connection refused"));
        }
    }

    @Test
    public void testDebugTrue() {
        var b1 = new RequestBuilder();
        b1
                .debug()
                .setUrl("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");

        assertTrue(b1.getDebug());
    }

    @Test
    public void testDebugFalse() {
        var b1 = new RequestBuilder();
        b1.debug().get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");

        var b2 = new RequestBuilder();
        b2.setUrl("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");

        assertFalse(b1.getDebug());
        assertFalse(b2.getDebug());
    }

    @Test
    public void testPathParams() {
        var r = http.get("https://run.mocky.io/v3/{id}", "2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");

        assertEquals(r.asString(), """
                {"a":1}""");
    }

    @Test
    public void testCheckErrorsIfErrorsExist() {
        try {
            http
                    .checkNoErrors(true)
                    .get("https://run.mocky.io/v3/85effc81-8aa7-4da2-963e-89cda64840b5");
        } catch (AssertionError assertionError) {
            assertTrue(assertionError.getMessage().startsWith("Response has errors!"));
        }
    }

    @Test
    public void testCheckErrorsIfNoErrorsInArrayResponse() {
        http
                .checkNoErrors(true)
                .get("https://run.mocky.io/v3/fa8898cd-81ac-43f7-8601-0c4a49e09fdb");
    }

    @Test
    public void testCheckErrorsIfHasErrorsInArrayResponse() {
        try {
            http
                    .checkNoErrors(true)
                    .get("https://run.mocky.io/v3/a481192b-83fc-46a0-a114-287d4c2e49d6");
        } catch (AssertionError assertionError) {
            assertTrue(assertionError.getMessage().startsWith("Response has errors!"));
        }
    }

    @Test
    public void testCheckNoErrorsIfHasErrorsInArrayResponse() {
        http
                .checkNoErrors(false)
                .get("https://run.mocky.io/v3/a481192b-83fc-46a0-a114-287d4c2e49d6");
    }

    @Test
    public void testErrorsNotCheckedIfFalse() {
        http
                .checkNoErrors(false)
                .get("https://run.mocky.io/v3/85effc81-8aa7-4da2-963e-89cda64840b5");
    }


    @Test
    public void testErrorsNotCheckedIfNull() {
        http
                .get("https://run.mocky.io/v3/85effc81-8aa7-4da2-963e-89cda64840b5");
    }

    @Test
    public void testNoExceptionIfCheckErrorsTrueAndNoErrorsInResponse() {
        http
                .checkNoErrors(true)
                .get("https://run.mocky.io/v3/2b6e17a9-a348-4211-8c8e-4dbdc151bf9a");
    }

    @Test
    public void testNoExceptionIfCheckNoErrorsNull() {
        http
                .checkNoErrors(false)
                .get("https://run.mocky.io/v3/85effc81-8aa7-4da2-963e-89cda64840b5");
    }
}

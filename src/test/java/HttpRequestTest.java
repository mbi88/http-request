import com.mbi.HttpRequest;
import com.mbi.RequestBuilder;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

public class HttpRequestTest {

    private HttpRequest http = new RequestBuilder();

    @Test
    public void test() {
        RequestSpecification specification = given()
                .header("asd", "")
                .body("asd");
        http
                .setRequestSpecification(specification)
                .setData(100)
                .setExpectedStatusCode(400)
                .setHeader("1", "1")
                .setHeader("2", "2")
                .setToken("asd")
                .setHeader("Cookie", "asd")
                .get("https://google.com");
    }

    @Test
    public void testSetHeader() {
        try {
            http
                    .setHeader("header1", "v")
                    .setHeader("header2", "v")
                    .get("asd");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'header1: v' -H 'header2: v'"));
        }
    }

    @Test
    public void testSetHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("h1", "v"));
        headers.add(new Header("h2", "v"));

        try {
            http
                    .setHeaders(headers)
                    .get("ads");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'h1: v' -H 'h2: v'"));
        }
    }

    @Test
    public void testWithoutHeaders() {
        try {
            http.get("ads");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("curl -X GET 'ads' " +
                    "-H 'Accept: application/json' -H 'Content-Type: application/json; charset=UTF-8'\n\n"));
        }
    }

    @Test
    public void testHeadersWithSpec() {
        RequestSpecification spec = given().header("h1", "v");
        try {
            http
                    .setRequestSpecification(spec)
                    .setHeader("h2", "v").get("asd");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'h1: v' -H 'h2: v'"));
        }
    }

    @Test
    public void testSetBody() {
        try {
            http.setData(1).get("asd");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("--data '1'\n\n"));
        }
    }

    @Test
    public void testWithoutBody() {
        try {
            http.get("asd");
        } catch (AssertionError error) {
            assertFalse(error.getMessage().contains(" --data '"));
        }
    }

    @Test
    public void testBodyOverridesSpec() {
        RequestSpecification spec = given().body(1);
        try {
            http
                    .setRequestSpecification(spec)
                    .setData(2)
                    .get("asd");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("--data '2'\n\n"));
        }
    }

    @Test
    public void testSetToken() {
        try {
            http
                    .setToken("token")
                    .get("asd");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'Authorization: token"));
        }
    }

    @Test
    public void testTokenWithSpec() {
        RequestSpecification spec = given().header(new Header("Authorization", "token1"));
        try {
            http
                    .setRequestSpecification(spec)
                    .setToken("token2")
                    .get("asd");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'Authorization: token2"));
        }
    }

    @Test
    public void testWithoutToken() {
        try {
            http
                    .get("asd");
        } catch (AssertionError error) {
            assertFalse(error.getMessage().contains("-H 'Authorization:"));
        }
    }

    @Test
    public void testWithTokenInSpec() {
        RequestSpecification spec = given().header(new Header("Authorization", "token1"));
        try {
            http
                    .setRequestSpecification(spec)
                    .get("asd");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'Authorization: token1"));
        }
    }

    @Test
    public void testCodeFail() {
        boolean passed;
        try {
            http
                    .setExpectedStatusCode(404)
                    .get("https://google.com");
            passed = true;
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("Expected status code <404> but was <200>"));
            passed = false;
        }

        assertFalse(passed);
        http.get("https://google.com");
    }

    @Test
    public void testCodeSuccess() {
        http
                .setExpectedStatusCode(404)
                .get("https://google.com/asdasd");
    }

    @Test
    public void testWithoutCode() {
        http.get("https://google.com/asdasd");
    }

    @Test
    public void testSpec() {
        RequestSpecification spec = given()
                .header(new Header("Authorization", "token1"))
                .body(1);

        try {
            http
                    .setRequestSpecification(spec)
                    .get("asd");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("curl -X GET 'asd' -H 'Accept: application/json' " +
                    "-H 'Authorization: token1' -H 'Content-Type: application/json; charset=UTF-8' --data '1'\n\n"));
        }
    }

    @Test
    public void testWithoutSpec() {
        try {
            http.get("asd");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("curl -X GET 'asd' -H 'Accept: application/json' " +
                    "-H 'Content-Type: application/json; charset=UTF-8'\n\n"));
        }
    }

    @Test
    public void testGet() {
        http.setExpectedStatusCode(200).get("https://httpbin.org/get");
    }

    @Test
    public void testPost() {
        http.setExpectedStatusCode(200).post("https://httpbin.org/post");
    }

    @Test
    public void testDelete() {
        http.setExpectedStatusCode(200).delete("https://httpbin.org/delete");
    }

    @Test
    public void testPatch() {
        http.setExpectedStatusCode(200).patch("https://httpbin.org/patch");
    }

    @Test
    public void testPut() {
        http.setExpectedStatusCode(200).put("https://httpbin.org/put");
    }

    @Test
    public void testBuildersResetAfterRequest() {
        try {
            http.setData(1).get("asd");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("--data '1'\n\n"));
        }

        try {
            http.get("asd");
        } catch (AssertionError error) {
            assertFalse(error.getMessage().endsWith("--data '1'\n\n"));
        }
    }

    @Test
    public void testThreadLocalIsSingleContainerPerInstanceNotPerClass() {
        HttpRequest httpRequest1 = new RequestBuilder();
        HttpRequest httpRequest2 = new RequestBuilder();

        httpRequest1
                .setData("123")
                .setToken(httpRequest2.get("https://google.com").asString());

        assertEquals(((RequestBuilder) httpRequest1).getData().toString(), "123");
        assertNull(((RequestBuilder) httpRequest2).getData());
    }
}

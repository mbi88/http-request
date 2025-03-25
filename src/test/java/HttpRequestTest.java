import com.mbi.HttpRequest;
import com.mbi.config.RequestDirector;
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
    public void testSetToken() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setToken("token")
                .setExpectedStatusCode(4534)
                .get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertTrue(ex.getMessage().contains("-H 'Authorization: token"));
    }

    @Test
    public void testTokenWithSpec() {
        var spec = given().header(new Header("Authorization", "token1"));

        var ex = expectThrows(AssertionError.class, () -> http
                .setRequestSpecification(spec)
                .setExpectedStatusCode(24)
                .setToken("token2")
                .get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertTrue(ex.getMessage().contains("-H 'Authorization: token2"));
    }

    @Test
    public void testWithoutToken() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setExpectedStatusCode(356)
                .get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertFalse(ex.getMessage().contains("-H 'Authorization:"));
    }

    @Test
    public void testWithTokenInSpec() {
        var spec = given().header(new Header("Authorization", "token1"));

        var ex = expectThrows(AssertionError.class, () -> http
                .setExpectedStatusCode(463)
                .setRequestSpecification(spec)
                .get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertTrue(ex.getMessage().contains("-H 'Authorization: token1"));
    }

    @Test
    public void testBodyOverridesSpec() {
        var spec = given().body(1);

        var ex = expectThrows(AssertionError.class, () -> http
                .setRequestSpecification(spec)
                .setExpectedStatusCode(234)
                .setData(2)
                .get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertTrue(ex.getMessage().endsWith("--data '2'\n\n"));
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
                .get("https://api.npoint.io/3a360af4f1419f85f238"));

        assertTrue(ex.getMessage().contains("--data '100'"), "Incorrect --data");
        assertTrue(ex.getMessage().contains("-H 'spec_header: spec_header_value' -H 'h1: h1_spec' -H 'Authorization: token' -H 'h1: h1_value' -H 'h2: h2_value' -H 'Cookie: cookie'"), "Incorrect headers");
    }

    @Test
    public void testCheckErrorsIfErrorsExist() {
        var ex = expectThrows(AssertionError.class, () -> http
                .checkNoErrors(true)
                .get("https://api.npoint.io/1e9bfb4122b88f8f3582"));
        assertTrue(ex.getMessage().startsWith("Response has errors!"));
    }

    @Test
    public void testCheckErrorsIfErrorsExistAndFlagFalse() {
        http
                .checkNoErrors(false)
                .get("https://api.npoint.io/1e9bfb4122b88f8f3582");
    }

    @Test
    public void testCheckErrorsIfEmptyErrors() {
        http
                .checkNoErrors(true)
                .get("https://api.npoint.io/b200b3d9aae0c8ebdcaa");
    }

    @Test
    public void testCheckErrorsIfOneErrorNull() {
        var ex = expectThrows(AssertionError.class, () -> http
                .checkNoErrors(true)
                .get("https://api.npoint.io/f651b826630ca37714cb"));
        assertTrue(ex.getMessage().startsWith("Response has errors!"));
    }

    @Test
    public void testCheckErrorsIfErrorsNull() {
        http
                .checkNoErrors(true)
                .get("https://api.npoint.io/44d894f7413df3d8b9df");
    }

    @Test
    public void testCheckErrorsIfHasErrorsInArrayResponse() {
        var ex = expectThrows(AssertionError.class, () -> http
                .checkNoErrors(true)
                .get("https://api.npoint.io/2a74a26ec8871bed9caf"));
        assertTrue(ex.getMessage().startsWith("Response has errors!"));
    }

    @Test
    public void testCodeFail() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setExpectedStatusCode(403)
                .get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertTrue(ex.getMessage().contains("expected [403] but found [200]"));
    }

    @Test
    public void testStatusCodeIsAcceptedWhenInList() {
        http
                .setExpectedStatusCodes(List.of(200, 404))
                .get("https://api.npoint.io/3a360af4f1419f85f238");
    }

    @Test
    public void testAssertionThrownIfStatusCodeNotInList() {
        var builder = new RequestBuilder()
                .setUrl("https://api.npoint.io/3a360af4f1419f85f238")
                .setExpectedStatusCodes(List.of(404, 500));
        var ex = expectThrows(AssertionError.class, () -> builder.get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertTrue(ex.getMessage().contains("expected [404, 500] but found [200]"));
    }

    @Test
    public void testCodesFail() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setExpectedStatusCodes(List.of(404, 403, 405))
                .get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertTrue(ex.getMessage().contains("expected [404, 403, 405] but found [200]"));
    }

    @Test
    public void testSetBody() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setExpectedStatusCode(342)
                .setData(1)
                .get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertTrue(ex.getMessage().endsWith("--data '1'\n\n"));
    }

    @Test
    public void testSpecHeaders() {
        var spec = given().header("h1", "v");

        var ex = expectThrows(AssertionError.class, () -> http
                .setToken("wer")
                .setRequestSpecification(spec)
                .setExpectedStatusCode(342)
                .get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertTrue(ex.getMessage().contains("-H 'h1: v' -H 'Authorization: wer'"));
    }

    @Test
    public void testSetHeaders() {
        var headers = new ArrayList<Header>();
        headers.add(new Header("h1", "v"));
        headers.add(new Header("h2", "v"));

        var ex = expectThrows(AssertionError.class, () -> http
                .setHeaders(headers)
                .setExpectedStatusCode(300)
                .get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertTrue(ex.getMessage().contains("-H 'h1: v' -H 'h2: v'"));
    }

    @Test
    public void testSetHeader() {
        var ex = expectThrows(AssertionError.class, () -> http
                .setHeader("header1", "v")
                .setHeader("header2", "v")
                .setExpectedStatusCode(300)
                .get("https://api.npoint.io/3a360af4f1419f85f238"));
        assertTrue(ex.getMessage().contains("-H 'header1: v' -H 'header2: v'"));
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

        assertTrue(builder.getDebug());
        assertTrue(director.getRequestConfig().isDebug());
    }

    @Test
    public void testPost() {
        http
                .post("https://api.npoint.io/44d894f7413df3d8b9df");
    }

    @Test
    public void testDelete() {
        http
                .delete("https://api.npoint.io/44d894f7413df3d8b9df");
    }

    @Test
    public void testPatch() {
        http
                .patch("https://api.npoint.io/44d894f7413df3d8b9df");
    }

    @Test
    public void testPut() {
        http
                .put("https://api.npoint.io/44d894f7413df3d8b9df");
    }
}

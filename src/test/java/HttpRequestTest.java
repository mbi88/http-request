import com.mbi.HttpRequest;
import com.mbi.RequestBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class HttpRequestTest {

    private HttpRequest http = new RequestBuilder();

    @Test
    public void testName() {


        RequestSpecification specification = given()
                .header("asd", "")
                .body("asd");
        Response r = http
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
    public void test2() {
        http.get("http://google.com");
    }
}

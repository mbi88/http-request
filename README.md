# http-request

**Usage:**

```java
import com.mbi.HttpRequest;
import org.testng.annotations.Test;

class HttpRequestTest {

    private HttpRequest request = new RequestBuilder();

    @Test
    public void test() {
        request
            .setExpectedStatusCode(200)
            .setPath("https://google.com")
            .get();
    }
}
```

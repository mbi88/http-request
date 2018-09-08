[![Build Status](https://travis-ci.org/mbi88/http-request.svg?branch=master)](https://travis-ci.org/mbi88/http-request)
[![codecov](https://codecov.io/gh/mbi88/http-request/branch/master/graph/badge.svg)](https://codecov.io/gh/mbi88/http-request)
[![](https://jitpack.io/v/mbi88/http-request.svg)](https://jitpack.io/#mbi88/http-request)


## About
http-request

Based on:
- <a href="https://github.com/rest-assured/rest-assured">rest-assured</a> 

## Example

```java
import com.mbi.HttpRequest;
import org.testng.annotations.Test;

class HttpRequestTest {

    private HttpRequest request = new RequestBuilder();

    @Test
    public void test() {
        request
            .setExpectedStatusCode(200)
            .get("https://google.com");
    }
}
```

### Properties
Set properties `src/main/resources/http-request.yml`

```yaml
connectionTimeout: 600000
headers:
  Accept: application/json
  Content-Type: application/json; charset=UTF-8
```

## See also
- <a href="https://github.com/rest-assured/rest-assured">rest-assured</a>
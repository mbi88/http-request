[![Java CI with Gradle](https://github.com/mbi88/http-request/actions/workflows/gradle.yml/badge.svg)](https://github.com/mbi88/http-request/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/mbi88/http-request/branch/master/graph/badge.svg)](https://codecov.io/gh/mbi88/http-request)
[![Latest Version](https://img.shields.io/github/v/tag/mbi88/http-request?label=version)](https://github.com/mbi88/http-request/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

# http-request

Lightweight fluent library to build and send HTTP requests using Rest-Assured — with automatic validation, logging, and error messages in curl format.

---

## Features

✅ Perform HTTP requests using a fluent Java API  
✅ Automatically verify response status codes  
✅ Assert errors include curl-formatted request + response  
✅ Resets builder after each request (thread-safe)  
✅ Logs request and response to file via SLF4J (`file-logger`)  
✅ Supports default headers and timeouts via YAML config  
✅ Built on top of [rest-assured](https://github.com/rest-assured/rest-assured)

---

## Installation

<details>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation("com.github.mbi88:http-request:master-SNAPSHOT")
}
```

</details>

<details>
<summary>Gradle (Groovy DSL)</summary>

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    testImplementation 'com.github.mbi88:http-request:master-SNAPSHOT'
}
```

</details>

---

## Example

```java
import com.mbi.HttpRequest;
import com.mbi.request.RequestBuilder;
import org.testng.annotations.Test;

class HttpRequestTest {

    private final HttpRequest request = new RequestBuilder();

    @Test
    public void test() {
        request
                .setData("{\"name\": \"user\"}")
                .setHeader("Content-Type", "application/json")
                .setToken("Bearer xxxxxx")
                .setExpectedStatusCode(201)
                .post("https://your.url");
    }
}
```

---

## Configuration

Create a file `src/main/resources/http-request.yml`:

```yaml
connectionTimeout: 60000
maxResponseLength: 1024
headers:
  Accept: application/json
  Content-Type: application/json; charset=UTF-8
```
This allows you to avoid repeating headers or timeouts in each request.

---

## Logging

To enable request/response logging to file, add the following appender to `logback-test.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property name="LOG_PATTERN_FILE" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n"/>

    <property name="LOG_DIR" value="build/logs"/>
    <property name="LOG_FILE" value="${LOG_DIR}/http-request.log"/>

    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>${LOG_FILE}</file>
        <append>true</append>
        <encoder>
            <pattern>${LOG_PATTERN_FILE}</pattern>
        </encoder>
    </appender>

    <logger name="file-logger" level="INFO" additivity="false">
        <appender-ref ref="FILE"/>
    </logger>

    <root level="INFO">
        <appender-ref ref="FILE"/>
    </root>

</configuration>
```

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## See also

- [rest-assured](https://github.com/rest-assured/rest-assured)

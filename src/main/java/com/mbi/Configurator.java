package com.mbi;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Objects;

import static io.restassured.RestAssured.given;

/**
 * Configures request.
 */
class Configurator {

    private final RequestBuilder builder;
    private RequestSpecification spec;
    private final Configuration configuration;
    private final String url;
    private final String method;
    private final Integer statusCode;

    Configurator(final RequestBuilder builder) {
        this.builder = builder;
        this.url = builder.getUrl();
        this.method = builder.getMethod().toString();
        this.statusCode = builder.getStatusCode();
        configuration = readConfiguration();
        spec = configureRequest();
    }

    private Configuration readConfiguration() {
        final InputStream in = getClass().getClassLoader().getResourceAsStream("http-request.yml");
        if (Objects.isNull(in)) {
            return new Configuration();
        }

        return new Yaml().loadAs(in, Configuration.class);
    }

    private RequestSpecification configureRequest() {
        spec = given();

        // Set default headers
        if (!Objects.isNull(configuration.getHeaders())) {
            spec.headers(configuration.getHeaders());
        }

        // Set request timeout
        if (!Objects.isNull(configuration.getConnectionTimeout())) {
            final RestAssuredConfig config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", configuration.getConnectionTimeout())
                    .setParam("http.socket.timeout", configuration.getConnectionTimeout()));
            spec.config(config);
        }

        // Override specification
        if (builder.getSpecification() != null) {
            spec.spec(builder.getSpecification());
        }

        // Set or override token
        if (builder.getToken() != null) {
            spec.header("Authorization", builder.getToken());
        }

        // Set or override data
        if (builder.getData() != null) {
            spec.body(builder.getData().toString());
        }

        // Set or override headers
        if (builder.getHeaders() != null) {
            for (Header header : builder.getHeaders()) {
                spec.header(header);
            }
        }

        // Print debug info
        if (builder.getDebug()) {
            spec.log().everything();
        }

        return spec;
    }

    public RequestSpecification getSpec() {
        return this.spec;
    }

    public String getUrl() {
        return this.url;
    }

    public String getMethod() {
        return this.method;
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }
}

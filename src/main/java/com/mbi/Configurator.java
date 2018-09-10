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
    private final Configuration configuration;
    private final String url;
    private final String method;
    private final Integer statusCode;
    private int maxResponseLength;
    private RequestSpecification spec;

    Configurator(final RequestBuilder builder) {
        this.builder = builder;
        this.url = builder.getUrl();
        this.method = builder.getMethod().toString();
        this.statusCode = builder.getStatusCode();
        configuration = readConfiguration();
        spec = configureRequest();
    }

    private RequestSpecification configureRequest() {
        spec = given();

        setDefaultHeaders();
        setRequestTimeout();
        setSpecification();
        setToken();
        setData();
        appendHeaders();
        setDebug();
        setMaxResponseLength();

        return spec;
    }

    private Configuration readConfiguration() {
        final InputStream in = getClass().getClassLoader().getResourceAsStream("http-request.yml");
        if (Objects.isNull(in)) {
            return new Configuration();
        }

        return new Yaml().loadAs(in, Configuration.class);
    }

    private void setDefaultHeaders() {
        if (!Objects.isNull(configuration.getHeaders())) {
            spec.headers(configuration.getHeaders());
        }
    }

    private void setRequestTimeout() {
        if (!Objects.isNull(configuration.getConnectionTimeout())) {
            final RestAssuredConfig config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", configuration.getConnectionTimeout())
                    .setParam("http.socket.timeout", configuration.getConnectionTimeout()));
            spec.config(config);
        }
    }

    private void setSpecification() {
        if (builder.getSpecification() != null) {
            spec.spec(builder.getSpecification());
        }
    }

    private void setToken() {
        if (builder.getToken() != null) {
            spec.header("Authorization", builder.getToken());
        }
    }

    private void setData() {
        if (builder.getData() != null) {
            spec.body(builder.getData().toString());
        }
    }

    private void appendHeaders() {
        if (builder.getHeaders() != null) {
            for (Header header : builder.getHeaders()) {
                spec.header(header);
            }
        }
    }

    private void setDebug() {
        if (builder.getDebug()) {
            spec.log().everything();
        }
    }

    private void setMaxResponseLength() {
        if (!Objects.isNull(configuration.getMaxResponseLength())) {
            this.maxResponseLength = configuration.getMaxResponseLength();
        }
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

    public int getMaxResponseLength() {
        return this.maxResponseLength;
    }
}

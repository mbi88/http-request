package com.mbi.config;

import com.mbi.request.RequestBuilder;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Header;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import static io.restassured.RestAssured.given;

/**
 * Builds and configures an HTTP request using a RequestBuilder and optional YAML configuration.
 */
public class RequestDirector {

    private final RequestBuilder requestBuilder;
    private final YamlConfiguration yamlConfiguration;
    private final RequestConfig requestConfig = new RequestConfig();

    /**
     * Constructs a director with a builder.
     *
     * @param requestBuilder DSL builder with thread-local data.
     */
    public RequestDirector(final RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
        this.yamlConfiguration = readYamlConfiguration();
    }

    /**
     * Returns the fully constructed request configuration.
     *
     * @return request config.
     */
    public RequestConfig getRequestConfig() {
        return this.requestConfig;
    }

    /**
     * Assembles and fills all request config fields based on builder and YAML.
     */
    public void constructRequest() {
        final RequestSpecification spec = configureRequest();

        requestConfig.setRequestSpecification(spec);
        requestConfig.setMethod(requestBuilder.getMethod());
        requestConfig.setUrl(requestBuilder.getUrl());
        requestConfig.setData(((FilterableRequestSpecification) spec).getBody());
        requestConfig.setHeaders(new ArrayList<>(((FilterableRequestSpecification) spec).getHeaders().asList()));
        requestConfig.setExpectedStatusCodes(requestBuilder.getStatusCodes());
        requestConfig.setPathParams(requestBuilder.getPathParams());
        requestConfig.setDebug(requestBuilder.getDebug());
        requestConfig.setMaxResponseLength(getMaxResponseLength());
        requestConfig.setCheckNoErrors(requestBuilder.getNoErrors());
    }

    /**
     * Reads configuration from 'http-request.yml' if present. Returns empty defaults if not found.
     */
    private YamlConfiguration readYamlConfiguration() {
        final InputStream in = getClass().getClassLoader().getResourceAsStream(yamlFileName());
        if (Objects.isNull(in)) {
            return new YamlConfiguration();
        }

        return new Yaml().loadAs(in, YamlConfiguration.class);
    }

    /**
     * Returns the YAML file name to read configuration from.
     */
    protected String yamlFileName() {
        return "http-request.yml";
    }

    /**
     * Creates and configures the Rest-Assured request.
     */
    private RequestSpecification configureRequest() {
        final RequestSpecification spec = given();

        setDefaultHeaders(spec);
        setRequestTimeout(spec);
        setSpecification(spec);
        setToken(spec);
        setData(spec);
        appendHeaders(spec);
        setDebug(spec);

        return spec;
    }

    /**
     * Applies default headers from YAML if present.
     */
    private void setDefaultHeaders(final RequestSpecification spec) {
        if (yamlConfiguration.getHeaders() != null) {
            spec.headers(yamlConfiguration.getHeaders());
        }
    }

    /**
     * Applies connection timeout from YAML config (if set).
     */
    private void setRequestTimeout(final RequestSpecification spec) {
        if (yamlConfiguration.getConnectionTimeout() != null) {
            final RestAssuredConfig config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", yamlConfiguration.getConnectionTimeout())
                    .setParam("http.socket.timeout", yamlConfiguration.getConnectionTimeout()));
            spec.config(config);
        }
    }

    /**
     * Applies user-defined request specification if provided.
     */
    private void setSpecification(final RequestSpecification spec) {
        if (requestBuilder.getSpecification() != null) {
            spec.spec(requestBuilder.getSpecification());
        }
    }

    /**
     * Sets Authorization token from the builder.
     */
    private void setToken(final RequestSpecification spec) {
        if (requestBuilder.getToken() != null) {
            spec.header("Authorization", requestBuilder.getToken());
        }
    }

    /**
     * Sets request body data.
     */
    private void setData(final RequestSpecification spec) {
        if (requestBuilder.getData() != null) {
            spec.body(requestBuilder.getData().toString());
        }
    }

    /**
     * Appends additional headers from the builder.
     */
    private void appendHeaders(final RequestSpecification spec) {
        if (requestBuilder.getHeaders() != null) {
            for (Header header : requestBuilder.getHeaders()) {
                spec.header(header);
            }
        }
    }

    /**
     * Enables debug logging if requested.
     */
    private void setDebug(final RequestSpecification spec) {
        if (requestBuilder.getDebug()) {
            spec.log().everything();
        }
    }

    /**
     * Reads max response length from YAML config (default = 0 = unlimited).
     */
    private int getMaxResponseLength() {
        return yamlConfiguration.getMaxResponseLength() != null
                ? yamlConfiguration.getMaxResponseLength()
                : 0;
    }
}

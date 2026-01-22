package com.mbi.config;

import com.mbi.request.RequestBuilder;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
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
    private final RequestConfig requestConfig = new RequestConfig();
    private YamlConfiguration yamlConfiguration;

    /**
     * Constructs a director with a builder.
     *
     * @param requestBuilder DSL builder with thread-local data.
     */
    public RequestDirector(final RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
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
        // Load YAML configuration to avoid calling an overridable method during object construction
        this.yamlConfiguration = readYamlConfiguration();

        final RequestSpecification spec = configureRequest();

        requestConfig.setRequestSpecification(spec);
        requestConfig.setMethod(requestBuilder.getMethod());
        requestConfig.setUrl(requestBuilder.getUrl());
        requestConfig.setData(((FilterableRequestSpecification) spec).getBody());
        requestConfig.setHeaders(new ArrayList<>(((FilterableRequestSpecification) spec).getHeaders().asList()));
        requestConfig.setExpectedStatusCodes(requestBuilder.getStatusCodes());
        requestConfig.setPathParams(requestBuilder.getPathParams());
        requestConfig.setDebug(requestBuilder.isDebug());
        requestConfig.setMaxResponseLength(getMaxResponseLength());
        requestConfig.setCheckNoErrors(requestBuilder.hasNoErrors());
    }

    /**
     * Reads configuration from 'http-request.yml' if present. Returns empty defaults if not found.
     */
    private YamlConfiguration readYamlConfiguration() {
        final InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(yamlFileName());
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
     * Applies request timeouts.
     * <p>
     * Always sets safe defaults to prevent infinite hangs.
     * If YAML defines connectionTimeout, it overrides connect + socket timeouts.
     */
    private void setRequestTimeout(final RequestSpecification spec) {
        // Safe defaults (ms)
        final int defaultConnectTimeoutMs = 10_000;
        final int defaultSocketTimeoutMs = 60_000;
        final int defaultPoolTimeoutMs = 10_000;

        // YAML override (if present). Current YAML model has only one value,
        // so we apply it to connect + socket.
        final Integer yamlTimeout = yamlConfiguration.getConnectionTimeout();
        final int connectTimeoutMs = (yamlTimeout != null) ? yamlTimeout : defaultConnectTimeoutMs;
        final int socketTimeoutMs = (yamlTimeout != null) ? yamlTimeout : defaultSocketTimeoutMs;
        final  int poolTimeoutMs = (yamlTimeout != null) ? yamlTimeout : defaultPoolTimeoutMs;

        final RestAssuredConfig config = RestAssured.config().httpClient(
                HttpClientConfig.httpClientConfig()
                        // Connect timeout
                        .setParam("http.connection.timeout", connectTimeoutMs)
                        // Read/socket timeout (prevents hanging on response read forever)
                        .setParam("http.socket.timeout", socketTimeoutMs)
                        // Wait timeout when connection pool is exhausted (important for parallel runs)
                        .setParam("http.connection-manager.timeout", poolTimeoutMs)
        );

        spec.config(config);
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
            for (final var header : requestBuilder.getHeaders()) {
                spec.header(header);
            }
        }
    }

    /**
     * Enables debug logging if requested.
     */
    private void setDebug(final RequestSpecification spec) {
        if (requestBuilder.isDebug()) {
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

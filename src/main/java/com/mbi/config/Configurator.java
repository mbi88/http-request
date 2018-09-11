package com.mbi.config;

import com.mbi.request.RequestBuilder;
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
public class Configurator {

    private final RequestBuilder builder;
    private final Configuration configuration;
    private int maxResponseLength;

    public Configurator(final RequestBuilder builder) {
        this.builder = builder;
        configuration = readConfiguration();
    }

    private RequestSpecification configureRequest() {
        final RequestSpecification spec = given();

        setDefaultHeaders(spec);
        setRequestTimeout(spec);
        setSpecification(spec);
        setToken(spec);
        setData(spec);
        appendHeaders(spec);
        setDebug(spec);
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

    private void setDefaultHeaders(final RequestSpecification spec) {
        if (!Objects.isNull(configuration.getHeaders())) {
            spec.headers(configuration.getHeaders());
        }
    }

    private void setRequestTimeout(final RequestSpecification spec) {
        if (!Objects.isNull(configuration.getConnectionTimeout())) {
            final RestAssuredConfig config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", configuration.getConnectionTimeout())
                    .setParam("http.socket.timeout", configuration.getConnectionTimeout()));
            spec.config(config);
        }
    }

    private void setSpecification(final RequestSpecification spec) {
        if (builder.getSpecification() != null) {
            spec.spec(builder.getSpecification());
        }
    }

    private void setToken(final RequestSpecification spec) {
        if (builder.getToken() != null) {
            spec.header("Authorization", builder.getToken());
        }
    }

    private void setData(final RequestSpecification spec) {
        if (builder.getData() != null) {
            spec.body(builder.getData().toString());
        }
    }

    private void appendHeaders(final RequestSpecification spec) {
        if (builder.getHeaders() != null) {
            for (Header header : builder.getHeaders()) {
                spec.header(header);
            }
        }
    }

    private void setDebug(final RequestSpecification spec) {
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
        return configureRequest();
    }

    public String getUrl() {
        return this.builder.getUrl();
    }

    public String getMethod() {
        return this.builder.getMethod().toString();
    }

    public Integer getStatusCode() {
        return this.builder.getStatusCode();
    }

    public int getMaxResponseLength() {
        return this.maxResponseLength;
    }
}

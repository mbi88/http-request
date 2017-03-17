package com.mbi.configuration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SSLConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import org.apache.http.conn.ssl.SSLSocketFactory;
import com.mbi.Parameters;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

public class Configurator {

    private RestAssuredConfig getConfig(String domain) {
        SSLSocketFactory customSslFactory = null;
        try {
            customSslFactory = new GatewaySslSocketFactory(domain,
                    SSLContext.getDefault(),
                    ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        RestAssuredConfig restAssuredConfig = RestAssured
                .config()
                .sslConfig(SSLConfig.sslConfig().sslSocketFactory(customSslFactory));

        restAssuredConfig
                .getHttpClientConfig()
                .reuseHttpClientInstance();

        return restAssuredConfig;
    }

    private String resolvePath(String path) {
        return path.replaceFirst("^https://(www.)?", "").split("/")[0];
    }

    private RequestSpecification defaultSpecification(String path) {
        RequestSpecification specification = given()
                .contentType(ContentType.JSON);

        if (path.startsWith("https"))
            specification.config(getConfig(resolvePath(path)));

        return specification;
    }

    public RequestSpecification configureRequest(Parameters requestParameters) {
        RequestSpecification spec = defaultSpecification(requestParameters.getPath());

        if (requestParameters.getSpecification() != null)
            spec.specification(requestParameters.getSpecification());

        if (requestParameters.getToken() != null) {
            spec.header("Authorization", requestParameters.getToken());
        }

        if (requestParameters.getData() != null) {
            spec.body(requestParameters.getData().toString());
        }

        return spec;
    }
}

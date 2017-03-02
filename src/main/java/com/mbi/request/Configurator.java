package com.mbi.request;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SSLConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

interface Configurator {

    static RestAssuredConfig getConfig(String domain) {
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

    static String resolvePath(String path) {
        return path.replaceFirst("^http(s)?://(www.)?", "").split("/")[0];
    }

    static RequestSpecification defaultSpecification(String path) {
        return given()
                .contentType(ContentType.JSON)
                .config(getConfig(resolvePath(path)));
    }

    default RequestSpecification configureRequest(String path) {
        return defaultSpecification(path).when();
    }

    default <T> RequestSpecification configureRequest(String path, T data, String token) {
        return defaultSpecification(path)
                .body(data.toString())
                .header("Authorization", token)
                .when();
    }

    default RequestSpecification configureRequest(String path, String token) {
        return defaultSpecification(path)
                .header("Authorization", token)
                .when();
    }

    default <T> RequestSpecification configureRequest(String path, T data) {
        return defaultSpecification(path)
                .body(data.toString())
                .when();
    }
}

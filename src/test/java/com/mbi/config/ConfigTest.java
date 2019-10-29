package com.mbi.config;

import com.mbi.request.RequestBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

class ConfigTest {

    @Test
    void configTest() {
        var config = new RequestConfig();
        config.setExpectedStatusCode(22);
        config.setHeaders(List.of(new Header("a", "3")));

        var requestBuilder = new RequestBuilder();
        requestBuilder.setConfig(config);
        requestBuilder.setData(1);
        requestBuilder.setHeader("a", "1");
        requestBuilder.setHeader("bb", "1");

        RequestDirector requestDirector = new RequestDirector();
//        requestDirector.setValuesFromConfigFile();
//        requestDirector.setValuesFromConfigObject(requestBuilder.getConfig());
//        requestDirector.readYamlConfiguration(getClass().getClassLoader().getResourceAsStream("http-request.yml"));
//        requestDirector.setValuesFromBuilder(requestBuilder);
        requestDirector.constructRequest(requestBuilder);
    }
}

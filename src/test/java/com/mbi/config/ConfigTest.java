package com.mbi.config;

import com.mbi.request.RequestBuilder;
import org.junit.jupiter.api.Test;

public class ConfigTest {

    @Test
    public void configTest() {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.setData(1);

        RequestDirector requestDirector = new RequestDirector(requestBuilder);
        requestDirector.setValuesFromConfigFile();
    }
}

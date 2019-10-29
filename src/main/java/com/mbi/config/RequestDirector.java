package com.mbi.config;

import com.mbi.request.RequestBuilder;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Configures request.
 */
public class RequestDirector {

    private RequestConfig requestConfig = new RequestConfig();

    public RequestConfig getRequestConfig() {
        return this.requestConfig;
    }

    public void constructRequest(final RequestBuilder requestBuilder) {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.getConfiguration().setCollectionsMergeEnabled(false);

        // load values from config file
        requestConfig = setValuesFromConfigFile();

        // set values from passed configuration
        if (requestBuilder.getConfig() != null) {
            modelMapper.map(setValuesFromConfigObject(requestBuilder.getConfig()), requestConfig);
        }

        // set values from passed arguments
        var argumentsConfig = setValuesFromBuilder(requestBuilder);
        // Merge headers
        argumentsConfig.getHeaders().addAll(requestConfig.getHeaders());
        modelMapper.map(argumentsConfig, requestConfig);
        setToken(requestBuilder);
    }

    protected RequestConfig setValuesFromConfigFile() {
        final var modelMapper = new ModelMapper();

        var propertyMap = new PropertyMap<YamlConfiguration, RequestConfig>() {
            @Override
            protected void configure() {
                // Map headers
                Converter<Map<String, String>, List<Header>> headersConverter = ctx -> {
                    if (ctx.getSource() == null) {
                        return new ArrayList<>();
                    }
                    return ctx.getSource().entrySet().stream()
                            .map(entry -> new Header(entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList());
                };
                using(headersConverter).map(source.getHeaders()).setHeaders(null);

                // Map response length
                Converter<Integer, Integer> responseConverter = ctx -> ctx.getSource() == null ? 0 : ctx.getSource();
                using(responseConverter).map().setMaxResponseLength(source.getMaxResponseLength());

                // Map timeout
                map().setRequestTimeOut(source.getConnectionTimeout());
            }
        };

        final var inputStream = getClass().getClassLoader().getResourceAsStream("http-request.yml");
        final var yamlConfiguration = readYamlConfiguration(inputStream);

        return modelMapper.addMappings(propertyMap).map(yamlConfiguration);
    }

    protected RequestConfig setValuesFromConfigObject(final RequestConfig config) {
        return new ModelMapper().map(config, RequestConfig.class);
    }

    protected RequestConfig setValuesFromBuilder(final RequestBuilder requestBuilder) {
        var modelMapper = new ModelMapper();

        var propertyMap = new PropertyMap<RequestBuilder, RequestConfig>() {

            @Override
            protected void configure() {
                map().setExpectedStatusCode(source.getStatusCode());
            }
        };

        return modelMapper.addMappings(propertyMap).map(requestBuilder);
    }

    protected YamlConfiguration readYamlConfiguration(final InputStream in) {
        if (Objects.isNull(in)) {
            return new YamlConfiguration();
        }

        return new Yaml().loadAs(in, YamlConfiguration.class);
    }

    private void setToken(final RequestBuilder requestBuilder) {
        if (requestBuilder.getToken() != null) {
            var headers = requestBuilder.getHeaders() == null ? new ArrayList<Header>() : requestBuilder.getHeaders();
            headers.add(new Header("Authorization", requestBuilder.getToken()));
            requestConfig.setHeaders(headers);
        }
    }
}

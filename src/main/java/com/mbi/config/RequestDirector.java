package com.mbi.config;

import com.mbi.request.RequestBuilder;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Configures request.
 */
public class RequestDirector {

    private final RequestBuilder requestBuilder;
    private final YamlConfiguration yamlConfiguration;
    private RequestConfig requestConfig = new RequestConfig();

    public RequestDirector(final RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;

        final var inputStream = getClass().getClassLoader().getResourceAsStream("http-request.yml");
        yamlConfiguration = readYamlConfiguration(inputStream);
    }

    public RequestConfig getRequestConfig() {
        return this.requestConfig;
    }

    public void constructRequest() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.getConfiguration().setCollectionsMergeEnabled(false);

        // load values from config file
        requestConfig = setValuesFromConfigFile();
        System.out.println(requestConfig.toString());

        // set values from passed configuration
        if (requestBuilder.getConfig() != null) {
            modelMapper.map(setValuesFromConfigObject(), requestConfig);
            System.out.println(requestConfig.toString());
        }

        // set values from passed arguments
        modelMapper.map(setValuesFromBuilder(), requestConfig);
        setToken();
        System.out.println(requestConfig.toString());
    }

    protected RequestConfig setValuesFromConfigFile() {
        final var modelMapper = new ModelMapper();

        var propertyMap = new PropertyMap<YamlConfiguration, RequestConfig>() {
            @Override
            protected void configure() {
                // Map headers
                modelMapper.createTypeMap(Map.class, List.class)
                        .setConverter(ctx -> {
                            if (ctx.getSource() == null) {
                                return new ArrayList();
                            }
                            var map = (Map<String, String>) ctx.getSource();
                            return map.entrySet().stream().map(entry -> new Header(entry.getKey(), entry.getValue()))
                                    .collect(Collectors.toList());
                        });

                // Map response length
                Converter<Integer, Integer> responseConverter = ctx -> ctx.getSource() == null ? 0 : ctx.getSource();
                using(responseConverter).map().setMaxResponseLength(source.getMaxResponseLength());

                // Map timeout
                map().setRequestTimeOut(source.getConnectionTimeout());
            }
        };

        return modelMapper.addMappings(propertyMap).map(yamlConfiguration);
    }

    protected RequestConfig setValuesFromConfigObject() {
        return new ModelMapper().map(requestBuilder.getConfig(), RequestConfig.class);
    }

    protected RequestConfig setValuesFromBuilder() {
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

    private void setToken() {
        if (requestBuilder.getToken() != null) {
            var headers = requestBuilder.getHeaders() == null ? new ArrayList<Header>() : requestBuilder.getHeaders();
            headers.add(new Header("Authorization", requestBuilder.getToken()));
            requestConfig.setHeaders(headers);
        }
    }
}

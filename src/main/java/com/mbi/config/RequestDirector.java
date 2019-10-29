package com.mbi.config;

import com.mbi.request.RequestBuilder;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configures request.
 */
public class RequestDirector {

    private RequestConfig requestConfig = new RequestConfig();
    private String yamlData;

    public RequestDirector() {
        setYamlData(getDataFromYamlFile("http-request.yml"));
    }

    protected void setYamlData(String yamlData) {
        this.yamlData = yamlData;
    }

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
        if (Objects.nonNull(argumentsConfig.getHeaders())) {
            argumentsConfig.getHeaders().addAll(requestConfig.getHeaders());
        }
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

        final var yamlConfiguration = readYamlConfiguration(yamlData);

        return modelMapper.addMappings(propertyMap).map(yamlConfiguration);
    }

    protected String getDataFromYamlFile(final String fileName) {
        Stream<String> lines = null;
        try {
            final Path path = Paths.get(Objects
                    .requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI());
            lines = Files.lines(path);
        } catch (IOException | URISyntaxException ignored) {
            // ignored
        }

        return Objects.requireNonNull(lines).collect(Collectors.joining("\n"));
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

    protected YamlConfiguration readYamlConfiguration(final String in) {
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

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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configures request.
 */
public class RequestDirector {

    private final Function<RequestConfig, List<Header>> extractHeaders = config -> config.getHeaders() == null
            ? List.of() : config.getHeaders();
    private final RequestConfig requestConfig = new RequestConfig();
    private String yamlData;

    public RequestDirector() {
        this("http-request.yml");
    }

    public RequestDirector(final String ymlConfigFile) {
        yamlData = getDataFromYamlFile(ymlConfigFile);
    }

    public RequestConfig getRequestConfig() {
        return this.requestConfig;
    }

    public void constructRequest(final RequestBuilder requestBuilder) {
        final var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.getConfiguration().setCollectionsMergeEnabled(false);

        // Load values from yaml config file
        final var fileConfig = getValuesFromConfigFile();
        modelMapper.map(fileConfig, requestConfig);

        // Get values from passed request configuration
        var builderConfig = new RequestConfig();
        if (requestBuilder.getConfig() != null) {
            builderConfig = getValuesFromConfigObject(requestBuilder.getConfig());
            modelMapper.map(builderConfig, requestConfig);
        }

        // Get values from passed arguments
        final var argumentsConfig = getValuesFromBuilder(requestBuilder);
        modelMapper.map(argumentsConfig, requestConfig);

        // Merge headers
        requestConfig.setHeaders(mergeHeaders(fileConfig, builderConfig, argumentsConfig));

        // Add authorization
        setToken(requestBuilder);
    }

    private List<Header> mergeHeaders(final RequestConfig fileConfig, final RequestConfig builderConfig,
                                      final RequestConfig argumentsConfig) {
        final var fileHeaders = extractHeaders.apply(fileConfig);
        final var builderHeaders = extractHeaders.apply(builderConfig);
        final var argHeaders = extractHeaders.apply(argumentsConfig);

        final var headers = new ArrayList<Header>();
        if (builderHeaders.isEmpty()) {
            headers.addAll(fileHeaders);
        }
        headers.addAll(builderHeaders);
        headers.addAll(argHeaders);

        return headers;
    }

    protected RequestConfig getValuesFromConfigFile() {
        final var modelMapper = new ModelMapper();

        final var propertyMap = new PropertyMap<YamlConfiguration, RequestConfig>() {
            @Override
            protected void configure() {
                // Map headers
                final Converter<Map<String, String>, List<Header>> headersConverter = ctx -> {
                    if (ctx.getSource() == null) {
                        return new ArrayList<>();
                    }
                    return ctx.getSource().entrySet().stream()
                            .map(entry -> new Header(entry.getKey(), entry.getValue())).collect(Collectors.toList());
                };
                using(headersConverter).map(source.getHeaders()).setHeaders(null);
                // Map response length
                final Converter<Integer, Integer> respConverter = ctx -> ctx.getSource() == null ? 0 : ctx.getSource();
                using(respConverter).map().setMaxResponseLength(source.getMaxResponseLength());
                // Map timeout
                map().setRequestTimeOut(source.getConnectionTimeout());
            }
        };

        final var yamlConfiguration = readYamlConfiguration(yamlData);

        return modelMapper.addMappings(propertyMap).map(Objects.requireNonNull(yamlConfiguration));
    }

    protected RequestConfig getValuesFromConfigObject(final RequestConfig config) {
        return new ModelMapper().map(config, RequestConfig.class);
    }

    protected RequestConfig getValuesFromBuilder(final RequestBuilder requestBuilder) {
        final var modelMapper = new ModelMapper();

        final var propertyMap = new PropertyMap<RequestBuilder, RequestConfig>() {
            @Override
            protected void configure() {
                map().setExpectedStatusCode(source.getStatusCode());
            }
        };

        return modelMapper.addMappings(propertyMap).map(requestBuilder);
    }

    private String getDataFromYamlFile(final String fileName) {
        final var url = getClass().getClassLoader().getResource(fileName);
        if (Objects.isNull(url)) {
            return null;
        }

        Stream<String> lines = null;
        try {
            final Path path = Paths.get(Objects.requireNonNull(url).toURI());
            lines = Files.lines(path);
        } catch (IOException | URISyntaxException ignored) {
            // ignored
        }

        return Objects.requireNonNull(lines).collect(Collectors.joining("\n"));
    }

    protected YamlConfiguration readYamlConfiguration(final String in) {
        if (Objects.isNull(in)) {
            return new YamlConfiguration();
        }

        return new Yaml().loadAs(in, YamlConfiguration.class);
    }

    private void setToken(final RequestBuilder requestBuilder) {
        if (requestBuilder.getToken() != null) {
            requestConfig.getHeaders().add(new Header("Authorization", requestBuilder.getToken()));
        }
    }
}

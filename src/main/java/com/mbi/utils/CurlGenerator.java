package com.mbi.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mbi.config.RequestConfig;
import io.restassured.http.Header;

import java.util.List;

/**
 * Generates a curl command representing the HTTP request.
 * Useful for debugging and reproducing API calls in a terminal.
 *
 * @param config request configuration.
 */
public record CurlGenerator(RequestConfig config) {

    /**
     * Builds a curl command string from the current request configuration.
     *
     * @return curl-formatted string.
     */
    public String getCurl() {
        final var curl = "curl"
                + getMethod()
                + getUrl()
                + getHeaders()
                + getData();

        return curl.contains("--data")
                ? curl
                : curl.replaceAll(" \\\\\n$", ""); // Remove trailing backslash if no data
    }

    /**
     * Appends HTTP method to the curl command (e.g. -X POST).
     *
     * @return formatted method string.
     */
    private String getMethod() {
        return String.format(" --request %s \\%n", config.getMethod());
    }

    /**
     * Appends the target URL to the curl command.
     *
     * @return formatted URL string.
     */
    private String getUrl() {
        return String.format("  --url '%s' \\%n", config.getUrl());
    }

    /**
     * Appends all configured headers to the curl command.
     *
     * @return formatted header string(s).
     */
    private String getHeaders() {
        final List<Header> headers = config.getHeaders();
        if (headers == null || headers.isEmpty()) {
            return "";
        }

        final var builder = new StringBuilder(20);
        for (final var header : headers) {
            builder.append("  --header '")
                    .append(header.getName())
                    .append(": ")
                    .append(header.getValue())
                    .append("' \\\n");
        }

        return builder.toString();
    }

    /**
     * Appends the body data to the curl command using --data flag.
     *
     * @return formatted body string, or empty string if no body is set.
     */
    private String getData() {
        final var data = config.getData();
        if (data == null) {
            return "";
        }

        // Pretty JSON with indentation
        final var pretty = prettyPrintJson(data.toString());

        // Indent JSON block with 4 spaces
        final var indented = indentBlock(pretty);

        // IMPORTANT: data block is the ONLY part without trailing backslash
        return "  --data '\n" + indented + "'";
    }

    /**
     * Pretty prints JSON string with indentation.
     *
     * @param json raw JSON string.
     * @return pretty-printed JSON string.
     */
    private String prettyPrintJson(final String json) {
        final var prettyGson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping() // optional: do not escape characters like <, >, &
                .create();

        try {
            final var element = JsonParser.parseString(json);
            // If valid JSON, return pretty-printed version
            return prettyGson.toJson(element);
        } catch (JsonSyntaxException ex) {
            // If not valid JSON, return original string
            return json;
        }
    }

    /**
     * Indents each line of the given text block with 4 spaces.
     *
     * @param text input text block.
     * @return indented text block.
     */
    private String indentBlock(final String text) {
        return text.lines()
                // Indent each line with 4 spaces
                .map(line -> " ".repeat(4) + line)
                // Join lines with newline
                .reduce((a, b) -> a + "\n" + b)
                // Return original text if empty
                .orElse(text);
    }
}

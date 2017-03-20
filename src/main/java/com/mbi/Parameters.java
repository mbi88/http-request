package com.mbi;

import com.jayway.restassured.specification.RequestSpecification;

public class Parameters {
    private String path;
    private Object data;
    private int statusCode;
    private String token;
    private RequestSpecification specification;

    Parameters() {
    }

    ParametersBuilder newBuilder() {
        return this.new ParametersBuilder();
    }

    public String getPath() {
        return path;
    }

    public Object getData() {
        return data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getToken() {
        return token;
    }

    public RequestSpecification getSpecification() {
        return specification;
    }

    class ParametersBuilder {

        private ParametersBuilder() {
        }

        ParametersBuilder setPath(String path) {
            Parameters.this.path = path;
            return this;
        }

        ParametersBuilder setData(Object data) {
            Parameters.this.data = data;
            return this;
        }

        ParametersBuilder setStatusCode(int code) {
            Parameters.this.statusCode = code;
            return this;
        }

        ParametersBuilder setToken(String token) {
            Parameters.this.token = token;
            return this;
        }

        ParametersBuilder setSpecification(RequestSpecification specification) {
            Parameters.this.specification = specification;
            return this;
        }

        Parameters build() {
            return Parameters.this;
        }
    }
}

package request;

import com.jayway.restassured.response.Response;

public class HttpRequest extends Specification implements Request {

    @Override
    public Response get(String path, int statusCode, String token) {
        Response r = getSpecification(token).get(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response get(String path, String token) {
        return getSpecification(token).get(path);
    }

    @Override
    public Response get(String path, int statusCode) {
        Response r = getSpecification().get(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response post(String path, T data, int statusCode, String token) {
        Response r = getSpecification(data, token).post(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response post(String path, T data, String token) {
        return getSpecification(data, token).post(path);
    }

    @Override
    public <T> Response post(String path, T data, int statusCode) {
        Response r = getSpecification(data).post(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response post(String path, int statusCode, String token) {
        Response r = getSpecification(token).post(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response put(String path, T data, int statusCode, String token) {
        Response r = getSpecification(data, token).put(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response put(String path, T data, String token) {
        return getSpecification(data, token).put(path);
    }

    @Override
    public <T> Response put(String path, T data, int statusCode) {
        Response r = getSpecification(data).put(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response put(String path, int statusCode, String token) {
        Response r = getSpecification(token).put(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response patch(String path, T data, int statusCode, String token) {
        Response r = getSpecification(data, token).patch(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response patch(String path, T data, String token) {
        return getSpecification(data, token).patch(path);
    }

    @Override
    public <T> Response patch(String path, T data, int statusCode) {
        Response r = getSpecification(data).patch(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response patch(String path, int statusCode, String token) {
        Response r = getSpecification(token).patch(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response delete(String path, T data, int statusCode, String token) {
        Response r = getSpecification(data, token).delete(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response delete(String path, int statusCode, String token) {
        Response r = getSpecification(token).delete(path);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response delete(String path, String token) {
        return getSpecification(token).delete(path);
    }

    @Override
    public Response delete(String path, int statusCode) {
        Response r = getSpecification().delete(path);
        checkStatus(r, statusCode);

        return r;
    }
}

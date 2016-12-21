package request;

import com.jayway.restassured.response.Response;

public class HttpRequest extends Specification implements Request {

    @Override
    public Response get(String url, int statusCode, String token) {
        Response r = getSpecification(token).get(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response get(String url, String token) {
        return getSpecification(token).get(url);
    }

    @Override
    public Response get(String url, int statusCode) {
        Response r = getSpecification().get(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response post(String url, T data, int statusCode, String token) {
        Response r = getSpecification(data, token).post(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response post(String url, T data, String token) {
        return getSpecification(data, token).post(url);
    }

    @Override
    public <T> Response post(String url, T data, int statusCode) {
        Response r = getSpecification(data).post(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response post(String url, int statusCode, String token) {
        Response r = getSpecification(token).post(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response put(String url, T data, int statusCode, String token) {
        Response r = getSpecification(data, token).put(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response put(String url, T data, String token) {
        return getSpecification(data, token).put(url);
    }

    @Override
    public <T> Response put(String url, T data, int statusCode) {
        Response r = getSpecification(data).put(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response put(String url, int statusCode, String token) {
        Response r = getSpecification(token).put(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response patch(String url, T data, int statusCode, String token) {
        Response r = getSpecification(data, token).patch(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response patch(String url, T data, String token) {
        return getSpecification(data, token).patch(url);
    }

    @Override
    public <T> Response patch(String url, T data, int statusCode) {
        Response r = getSpecification(data).patch(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response patch(String url, int statusCode, String token) {
        Response r = getSpecification(token).patch(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public <T> Response delete(String url, T data, int statusCode, String token) {
        Response r = getSpecification(data, token).delete(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response delete(String url, int statusCode, String token) {
        Response r = getSpecification(token).delete(url);
        checkStatus(r, statusCode);

        return r;
    }

    @Override
    public Response delete(String url, String token) {
        return getSpecification(token).delete(url);
    }

    @Override
    public Response delete(String url, int statusCode) {
        Response r = getSpecification().delete(url);
        checkStatus(r, statusCode);

        return r;
    }
}

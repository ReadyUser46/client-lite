package dev.skuggi.core;

import dev.skuggi.models.RequestModel;
import dev.skuggi.models.ResponseModel;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;

import java.util.Map;

@Data
public class RestAssuredLite {

    private static final ThreadLocal<RestAssuredLite> instance = new ThreadLocal<>();
    private RequestModel requestModel;
    private final RequestSpecification client;
    private ResponseModel responseModel;

    public RestAssuredLite(boolean log) {
        client = log ? RestAssured.given().log().all() : RestAssured.given();
        requestModel = new RequestModel();
    }

    //-----------------------------------------------------------------------------------------------

    public static RestAssuredLite getInstance(boolean log) {
        if (instance.get() == null) instance.set(new RestAssuredLite(log));
        return instance.get();
    }

    public RestAssuredLite setBaseUri(String baseUri) {
        requestModel.setBaseUri(baseUri);
        return this;
    }

    public RestAssuredLite appendToBaseUri(String uri) {
        requestModel.appendBaseUri(uri);
        return this;
    }

    public RestAssuredLite setBasePath(String basePath) {
        requestModel.setBasePath(basePath);
        return this;
    }

    public RestAssuredLite appendToBasePath(String path) {
        requestModel.appendBasePath(path);
        return this;
    }

    public RestAssuredLite addHeader(String attr, Object value) {
        requestModel.getHeaders().put(attr, value);
        return this;
    }

    public RestAssuredLite addHeaders(Map<String, String> headers) {
        requestModel.getHeaders().putAll(headers);
        return this;
    }

    public RestAssuredLite setContentType(ContentType contentType) {
        requestModel.setContentType(contentType);
        return this;
    }

    public RestAssuredLite setBody(Object body) {
        requestModel.setBody(body);
        return this;
    }

    public RestAssuredLite setBasicAuth(String username, String pass) {
        client.auth().preemptive().basic(username, pass);
        return this;
    }

    //-----------------------------------------------------------------------------------------------

    public RestAssuredLite sendRequest(Method requestType, String endpoint, Object... params) {
        Response response;
        if (endpoint != null) response = client.request(requestType, endpoint, params);
        else response = client.request(requestType);

        responseModel.setBody(response.asPrettyString());
        responseModel.setStatusCode(response.getStatusCode());
        return this;
    }

    public RestAssuredLite sendRequest(Method requestType) {
        return sendRequest(requestType, null, (Object) null);
    }

    public int getStatusCode() {
        return responseModel.getStatusCode();
    }

    //-----------------------------------------------------------------------------------------------

    public RestAssuredLite buildRequest() {
        setTargetUrl();
        setHeaders();
        setContentType();
        return this;
    }

    private void setTargetUrl() {
        client.baseUri(requestModel.getBaseUri());
        client.basePath(requestModel.getBasePath());
    }

    private void setHeaders() {
        for (Map.Entry<String, Object> m : requestModel.getHeaders().entrySet()) {
            client.header(m.getKey(), m.getValue());
        }
    }

    private void setContentType() {
        if (requestModel.getContentType() == null) requestModel.setContentType(ContentType.JSON);
        client.contentType(requestModel.getContentType());
    }


}

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

    /**
     * @param log: enable or disable log().all()
     */
    public RestAssuredLite(boolean log) {
        client = log ? RestAssured.given().log().all() : RestAssured.given();
        requestModel = new RequestModel();
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * @param log: enable or disable log().all()
     * @return RestAssuredLite instance
     */
    public static RestAssuredLite getInstance(boolean log) {
        if (instance.get() == null) instance.set(new RestAssuredLite(log));
        return instance.get();
    }


    /**
     * @param baseUri string containing baseUri for request model
     * @return this
     */
    public RestAssuredLite setBaseUri(String baseUri) {
        requestModel.setBaseUri(baseUri);
        return this;
    }

    /**
     * @param uri string path to be concatenated to baseUri
     * @return this
     */
    public RestAssuredLite appendToBaseUri(String uri) {
        requestModel.appendBaseUri(uri);
        return this;
    }

    /**
     * @param basePath string containing basePath for request model
     * @return this
     */
    public RestAssuredLite setBasePath(String basePath) {
        requestModel.setBasePath(basePath);
        return this;
    }

    /**
     * @param path string to be concatenated to basePath
     * @return this
     */
    public RestAssuredLite appendToBasePath(String path) {
        requestModel.appendBasePath(path);
        return this;
    }

    /**
     * @param attr  string header name
     * @param value string header value
     * @return this
     */
    public RestAssuredLite addHeader(String attr, Object value) {
        requestModel.getHeaders().put(attr, value);
        return this;
    }

    /**
     * @param headers Map<String, Object> with name, value headers
     * @return this
     */
    public RestAssuredLite addHeaders(Map<String, Object> headers) {
        requestModel.getHeaders().putAll(headers);
        return this;
    }

    /**
     * @param contentType http ContentType
     * @return
     */
    public RestAssuredLite setContentType(ContentType contentType) {
        requestModel.setContentType(contentType);
        return this;
    }

    /**
     * @param body Object containing request body
     * @return
     */
    public RestAssuredLite setBody(Object body) {
        requestModel.setBody(body);
        return this;
    }

    public RestAssuredLite setBasicAuth(String username, String pass) {
        client.auth().preemptive().basic(username, pass);
        return this;
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * @param requestType Method type for request(GET,PUT,POST,PUT...)
     * @param endpoint    String endpointh to be targeted
     * @param params      array object containing pathParams
     * @return
     */
    public RestAssuredLite sendRequest(Method requestType, String endpoint, Object... params) {
        Response response;
        if (endpoint != null) response = client.request(requestType, endpoint, params);
        else response = client.request(requestType);

        responseModel.setBody(response.asPrettyString());
        responseModel.setStatusCode(response.getStatusCode());
        return this;
    }

    /**
     *
     * @param requestType Method type for request(GET,PUT,POST,PUT...)
     * @return this
     */
    public RestAssuredLite sendRequest(Method requestType) {
        return sendRequest(requestType, null, (Object) null);
    }

    /**
     *
     * @return response status code
     */
    public int getStatusCode() {
        return responseModel.getStatusCode();
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * set url, headers and content type for the request
     * @return this
     */
    public RestAssuredLite buildRequest() {
        setTargetUrl();
        setHeaders();
        setContentType();
        return this;
    }

    /**
     * set baseUri and basePath for the request
     */
    private void setTargetUrl() {
        client.baseUri(requestModel.getBaseUri());
        client.basePath(requestModel.getBasePath());
    }

    /**
     * loop through headers map and set each entry to header
     */
    private void setHeaders() {
        for (Map.Entry<String, Object> m : requestModel.getHeaders().entrySet()) {
            client.header(m.getKey(), m.getValue());
        }
    }

    /**
     * set contentType to request
     */
    private void setContentType() {
        if (requestModel.getContentType() == null) requestModel.setContentType(ContentType.JSON);
        client.contentType(requestModel.getContentType());
    }


}

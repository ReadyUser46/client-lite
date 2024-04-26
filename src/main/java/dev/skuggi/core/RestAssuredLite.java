package dev.skuggi.core;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import org.apache.http.HttpStatus;

import java.util.Map;

@Data
public class RestAssuredLite {

    private final RequestSpecification client;
    private Response response;
    private String url;


    public RestAssuredLite(boolean log) {
        client = log ? RestAssured.given().log().all() : RestAssured.given();
    }

    public static RestAssuredLite getInstance(boolean log) {
        return new RestAssuredLite(log);
    }


    public RestAssuredLite setUrl(String url) {
        this.url = url;
        return this;
    }

    public RestAssuredLite appendToUrl(String url) {
        this.url = this.url.concat(url);
        return this;
    }


    public RestAssuredLite addHeader(String attr, String value) {
        client.header(attr, value);
        return this;
    }

    public RestAssuredLite addHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> m : headers.entrySet()) {
            client.header(m.getKey(), m.getValue());
        }
        return this;
    }

    public RestAssuredLite setContentType(ContentType contentType) {
        client.contentType(contentType);
        return this;
    }

    public RestAssuredLite setBasicAuth(String username, String pass) {
        client.auth().preemptive().basic(username, pass);
        return this;
    }

    public RestAssuredLite sendRequest(Method requestType, Object body) {
        client.baseUri(url);
        client.body(body);
        response = client.request(requestType);
        return this;
    }

    public int getStatusCode() {
        return response.getStatusCode();
    }

    public RestAssuredLite requestGet(String endpoint, String... pathParams) {
        Response response =
                client
                        .when()
                        .get(endpoint, (Object) pathParams)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract().response();

        setResponse(response);

        return this;

    }

    public RestAssuredLite requestPost(String endpoint, String body, String... pathParams) {
        Response response =
                client
                        .body(body)
                        .when()
                        .post(endpoint, (Object) pathParams)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_CREATED)
                        .extract().response();

        setResponse(response);

        return this;

    }


}

package dev.skuggi.models;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseModel {

    private String body;
    private String error;
    private int statusCode;
    private Response response;
}

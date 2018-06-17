package ru.tkhozyaykina.apitest;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import ru.tkhozyaykina.apitest.precondion.Precondition;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Feature("httpbin.org")
public class PathHeadersTest extends Precondition {

    @Story("/headers")
    @Test(description = "Проверка JSON")
    public void testRequestInspection() {
        String url = RestAssured.baseURI + "headers";

        Response response = given()
                .relaxedHTTPSValidation()
                .get(url);

        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("headers.Connection", equalTo("close"))
                .body("headers.Host", equalTo("httpbin.org"))

                .header("Server", "gunicorn/19.8.1");
    }

    @Story("/response-headers")
    @Test(description = "GET")
    public void testResponseInspectionGet() {
        String headerPathUrl = RestAssured.baseURI + "response-headers";
        String keyParam = "freeform";
        String valuePram = "testResponseInspectionGet";
        int contentLengthDefault = 72;

        Response response = given()
                .relaxedHTTPSValidation()
                .param(keyParam, valuePram)
                .get(headerPathUrl);

        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("Content-Length", equalTo(String.valueOf(contentLengthDefault + valuePram.length())))
                .body(keyParam, equalTo(valuePram));
    }

    @Story("/response-headers")
    @Test(description = "POST")
    public void testResponseInspectionPost() {
        String headerPathUrl = RestAssured.baseURI + "response-headers";
        String keyParam = "freeform";
        String valuePram = "testResponseInspectionPost";
        int contentLengthDefault = 72;

        Response response = given()
                .relaxedHTTPSValidation()
                .queryParam(keyParam, valuePram)
                .post(headerPathUrl);

        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("Content-Length", equalTo(String.valueOf(contentLengthDefault + valuePram.length())))
                .body(keyParam, equalTo(valuePram));
    }
}


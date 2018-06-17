package ru.tkhozyaykina.apitest;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.tkhozyaykina.apitest.precondion.Precondition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.toList;

@Feature("httpbin.org")
@Story("/status/:code")
public class StatusCodeTest extends Precondition {

    @DataProvider(name = "StatusCodeDataProvider")
    public static Object[][] name() {
        return new Object[][]{{100}, {200}, {300}, {400}, {500}};
    }


    @Test(description = "Проверка GET", dataProvider = "StatusCodeDataProvider")
    public void testGet(int statusCode) {
        String url = RestAssured.baseURI + "status/" + statusCode;

        Response response = given().relaxedHTTPSValidation().get(url);

        response.then().statusCode(statusCode);
    }


    @Test(description = "Проверка POST", dataProvider = "StatusCodeDataProvider")
    public void testPost(int statusCode) {
        String url = RestAssured.baseURI + "status/" + statusCode;

        Response response = given().relaxedHTTPSValidation().post(url);

        response.then().statusCode(statusCode);
    }

    @Test(description = "Проверка PUT", dataProvider = "StatusCodeDataProvider")
    public void testPut(int statusCode) {
        String url = RestAssured.baseURI + "status/" + statusCode;

        Response response = given().relaxedHTTPSValidation().put(url);

        response.then().statusCode(statusCode);
    }

    @Test(description = "Проверка DELETE", dataProvider = "StatusCodeDataProvider")
    public void testDelete(int statusCode) {
        String url = RestAssured.baseURI + "status/" + statusCode;

        Response response = given().relaxedHTTPSValidation().delete(url);

        response.then().statusCode(statusCode);
    }


    @Test(description = "Проверка PATCH", dataProvider = "StatusCodeDataProvider")
    public void testPatch(int statusCode) {
        String url = RestAssured.baseURI + "status/" + statusCode;

        Response response = given().relaxedHTTPSValidation().patch(url);

        response.then().statusCode(statusCode);
    }
}


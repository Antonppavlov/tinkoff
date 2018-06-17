package ru.tkhozyaykina.apitest;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import org.apache.http.client.ClientProtocolException;
import org.testng.annotations.Test;
import ru.tkhozyaykina.apitest.precondion.Precondition;

import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.config;

@Feature("httpbin.org")
@Story("/redirect/:n")
public class TrackRedirectTest extends Precondition {

    @Test(description = "Успешный redirect")
    public void testSuccessfulRedirect() {
        int countRedirects = 11;
        String url = RestAssured.baseURI + "redirect/" + countRedirects;

        RestAssured.config = config().redirect(redirectConfig().followRedirects(true).and().maxRedirects(11));

        RestAssured.given()
                .config(RestAssured.config)
                .relaxedHTTPSValidation()
                .get(url);

    }

    @Test(description = "Кол-во redirect первышает максимум", expectedExceptions = ClientProtocolException.class)
    public void testExpectedClientProtocolException() {
        int countRedirects = 11;
        int maxRedirects = countRedirects - 1;

        String url = RestAssured.baseURI + "redirect/" + countRedirects;

        RestAssured.config = config().redirect(redirectConfig().followRedirects(true).and().maxRedirects(maxRedirects));

        RestAssured.given()
                .config(RestAssured.config)
                .relaxedHTTPSValidation()
                .get(url);

    }
}

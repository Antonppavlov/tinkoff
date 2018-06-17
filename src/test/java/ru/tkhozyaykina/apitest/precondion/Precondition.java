package ru.tkhozyaykina.apitest.precondion;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import ru.tkhozyaykina.apitest.Log4jRestAssuredFilter;

public class Precondition {

    private static boolean enable = false;

    @BeforeClass
    public void beforeClass() {
        if (!enable) {
            RestAssured.filters(
                    new AllureRestAssured(),
                    new Log4jRestAssuredFilter()
            );
            enable = true;
        }

        String baseUrl = "https://httpbin.org/";
        RestAssured.baseURI = baseUrl;
    }
}

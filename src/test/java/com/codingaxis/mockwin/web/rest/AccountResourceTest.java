package com.codingaxis.mockwin.web.rest;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.web.rest.vm.UserVM;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class AccountResourceTest {

    private static final TypeRef<UserVM> ENTITY_TYPE = new TypeRef<>() {
    };

    @BeforeAll
    static void jsonMapper() {
        RestAssured.config =
            RestAssured.config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(TestUtil.jsonbObjectMapper()));
    }

    @Test
    public void testGetExistingAccount() {
        var user = given()
            .auth()
            .preemptive()
            .oauth2(TestUtil.getAdminToken())
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/account")
            .then()
            .statusCode(OK.getStatusCode())
            .extract().as(ENTITY_TYPE);

        assertThat(user.login).isEqualTo("admin");

    }

    @Test
    public void testGetUnknownAccount() {
        given().contentType(APPLICATION_JSON).accept(APPLICATION_JSON).get("/api/account").then().statusCode(UNAUTHORIZED.getStatusCode());
    }
}

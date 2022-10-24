package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.UserType;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class UserTypeResourceTest {

    private static final TypeRef<UserType> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<UserType>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final String DEFAULT_USER_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_USER_TYPE = "BBBBBBBBBB";



    String adminToken;

    UserType userType;

    @Inject
    LiquibaseFactory liquibaseFactory;

    @BeforeAll
    static void jsonMapper() {
        RestAssured.config =
            RestAssured.config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(TestUtil.jsonbObjectMapper()));
    }

    @BeforeEach
    public void authenticateAdmin() {
        this.adminToken = TestUtil.getAdminToken();
    }

    @BeforeEach
    public void databaseFixture() {
        try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
            liquibase.dropAll();
            liquibase.validate();
            liquibase.update(liquibaseFactory.createContexts(), liquibaseFactory.createLabels());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserType createEntity() {
        var userType = new UserType();
        userType.userType = DEFAULT_USER_TYPE;
        return userType;
    }

    @BeforeEach
    public void initTest() {
        userType = createEntity();
    }

    @Test
    public void createUserType() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the UserType
        userType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userType)
            .when()
            .post("/api/user-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the UserType in the database
        var userTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userTypeList).hasSize(databaseSizeBeforeCreate + 1);
        var testUserType = userTypeList.stream().filter(it -> userType.id.equals(it.id)).findFirst().get();
        assertThat(testUserType.userType).isEqualTo(DEFAULT_USER_TYPE);
    }

    @Test
    public void createUserTypeWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the UserType with an existing ID
        userType.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userType)
            .when()
            .post("/api/user-types")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the UserType in the database
        var userTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userTypeList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateUserType() {
        // Initialize the database
        userType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userType)
            .when()
            .post("/api/user-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the userType
        var updatedUserType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types/{id}", userType.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the userType
        updatedUserType.userType = UPDATED_USER_TYPE;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedUserType)
            .when()
            .put("/api/user-types")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the UserType in the database
        var userTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userTypeList).hasSize(databaseSizeBeforeUpdate);
        var testUserType = userTypeList.stream().filter(it -> updatedUserType.id.equals(it.id)).findFirst().get();
        assertThat(testUserType.userType).isEqualTo(UPDATED_USER_TYPE);
    }

    @Test
    public void updateNonExistingUserType() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userType)
            .when()
            .put("/api/user-types")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the UserType in the database
        var userTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteUserType() {
        // Initialize the database
        userType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userType)
            .when()
            .post("/api/user-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the userType
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/user-types/{id}", userType.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var userTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllUserTypes() {
        // Initialize the database
        userType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userType)
            .when()
            .post("/api/user-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the userTypeList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(userType.id.intValue()))
            .body("userType", hasItem(DEFAULT_USER_TYPE));
    }

    @Test
    public void getUserType() {
        // Initialize the database
        userType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userType)
            .when()
            .post("/api/user-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the userType
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/user-types/{id}", userType.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the userType
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types/{id}", userType.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(userType.id.intValue()))
            
                .body("userType", is(DEFAULT_USER_TYPE));
    }

    @Test
    public void getNonExistingUserType() {
        // Get the userType
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-types/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

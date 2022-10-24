package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.ContestType;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class ContestTypeResourceTest {

    private static final TypeRef<ContestType> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<ContestType>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final String DEFAULT_CONTEST_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTEST_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";



    String adminToken;

    ContestType contestType;

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
    public static ContestType createEntity() {
        var contestType = new ContestType();
        contestType.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        contestType.contestType = DEFAULT_CONTEST_TYPE;
        contestType.description = DEFAULT_DESCRIPTION;
        return contestType;
    }

    @BeforeEach
    public void initTest() {
        contestType = createEntity();
    }

    @Test
    public void createContestType() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ContestType
        contestType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestType)
            .when()
            .post("/api/contest-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the ContestType in the database
        var contestTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestTypeList).hasSize(databaseSizeBeforeCreate + 1);
        var testContestType = contestTypeList.stream().filter(it -> contestType.id.equals(it.id)).findFirst().get();
        assertThat(testContestType.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testContestType.contestType).isEqualTo(DEFAULT_CONTEST_TYPE);
        assertThat(testContestType.description).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    public void createContestTypeWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ContestType with an existing ID
        contestType.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestType)
            .when()
            .post("/api/contest-types")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ContestType in the database
        var contestTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkContestTypeIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        contestType.contestType = null;

        // Create the ContestType, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestType)
            .when()
            .post("/api/contest-types")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ContestType in the database
        var contestTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateContestType() {
        // Initialize the database
        contestType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestType)
            .when()
            .post("/api/contest-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the contestType
        var updatedContestType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types/{id}", contestType.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the contestType
        updatedContestType.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedContestType.contestType = UPDATED_CONTEST_TYPE;
        updatedContestType.description = UPDATED_DESCRIPTION;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedContestType)
            .when()
            .put("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the ContestType in the database
        var contestTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestTypeList).hasSize(databaseSizeBeforeUpdate);
        var testContestType = contestTypeList.stream().filter(it -> updatedContestType.id.equals(it.id)).findFirst().get();
        assertThat(testContestType.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testContestType.contestType).isEqualTo(UPDATED_CONTEST_TYPE);
        assertThat(testContestType.description).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    public void updateNonExistingContestType() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
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
            .body(contestType)
            .when()
            .put("/api/contest-types")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ContestType in the database
        var contestTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteContestType() {
        // Initialize the database
        contestType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestType)
            .when()
            .post("/api/contest-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the contestType
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/contest-types/{id}", contestType.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var contestTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllContestTypes() {
        // Initialize the database
        contestType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestType)
            .when()
            .post("/api/contest-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the contestTypeList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(contestType.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("contestType", hasItem(DEFAULT_CONTEST_TYPE))            .body("description", hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    public void getContestType() {
        // Initialize the database
        contestType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestType)
            .when()
            .post("/api/contest-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the contestType
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/contest-types/{id}", contestType.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the contestType
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types/{id}", contestType.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(contestType.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("contestType", is(DEFAULT_CONTEST_TYPE))
                .body("description", is(DEFAULT_DESCRIPTION));
    }

    @Test
    public void getNonExistingContestType() {
        // Get the contestType
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-types/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

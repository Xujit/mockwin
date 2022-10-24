package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.CompetExamResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class CompetExamResponseResourceTest {

    private static final TypeRef<CompetExamResponse> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<CompetExamResponse>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";



    String adminToken;

    CompetExamResponse competExamResponse;

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
    public static CompetExamResponse createEntity() {
        var competExamResponse = new CompetExamResponse();
        competExamResponse.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        competExamResponse.name = DEFAULT_NAME;
        competExamResponse.description = DEFAULT_DESCRIPTION;
        return competExamResponse;
    }

    @BeforeEach
    public void initTest() {
        competExamResponse = createEntity();
    }

    @Test
    public void createCompetExamResponse() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the CompetExamResponse
        competExamResponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExamResponse)
            .when()
            .post("/api/compet-exam-responses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the CompetExamResponse in the database
        var competExamResponseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamResponseList).hasSize(databaseSizeBeforeCreate + 1);
        var testCompetExamResponse = competExamResponseList.stream().filter(it -> competExamResponse.id.equals(it.id)).findFirst().get();
        assertThat(testCompetExamResponse.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testCompetExamResponse.name).isEqualTo(DEFAULT_NAME);
        assertThat(testCompetExamResponse.description).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    public void createCompetExamResponseWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the CompetExamResponse with an existing ID
        competExamResponse.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExamResponse)
            .when()
            .post("/api/compet-exam-responses")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CompetExamResponse in the database
        var competExamResponseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamResponseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        competExamResponse.name = null;

        // Create the CompetExamResponse, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExamResponse)
            .when()
            .post("/api/compet-exam-responses")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CompetExamResponse in the database
        var competExamResponseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamResponseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateCompetExamResponse() {
        // Initialize the database
        competExamResponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExamResponse)
            .when()
            .post("/api/compet-exam-responses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the competExamResponse
        var updatedCompetExamResponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses/{id}", competExamResponse.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the competExamResponse
        updatedCompetExamResponse.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedCompetExamResponse.name = UPDATED_NAME;
        updatedCompetExamResponse.description = UPDATED_DESCRIPTION;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedCompetExamResponse)
            .when()
            .put("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the CompetExamResponse in the database
        var competExamResponseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamResponseList).hasSize(databaseSizeBeforeUpdate);
        var testCompetExamResponse = competExamResponseList.stream().filter(it -> updatedCompetExamResponse.id.equals(it.id)).findFirst().get();
        assertThat(testCompetExamResponse.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testCompetExamResponse.name).isEqualTo(UPDATED_NAME);
        assertThat(testCompetExamResponse.description).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    public void updateNonExistingCompetExamResponse() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
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
            .body(competExamResponse)
            .when()
            .put("/api/compet-exam-responses")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CompetExamResponse in the database
        var competExamResponseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamResponseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCompetExamResponse() {
        // Initialize the database
        competExamResponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExamResponse)
            .when()
            .post("/api/compet-exam-responses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the competExamResponse
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/compet-exam-responses/{id}", competExamResponse.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var competExamResponseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamResponseList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllCompetExamResponses() {
        // Initialize the database
        competExamResponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExamResponse)
            .when()
            .post("/api/compet-exam-responses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the competExamResponseList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(competExamResponse.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("name", hasItem(DEFAULT_NAME))            .body("description", hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    public void getCompetExamResponse() {
        // Initialize the database
        competExamResponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExamResponse)
            .when()
            .post("/api/compet-exam-responses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the competExamResponse
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/compet-exam-responses/{id}", competExamResponse.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the competExamResponse
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses/{id}", competExamResponse.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(competExamResponse.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("name", is(DEFAULT_NAME))
                .body("description", is(DEFAULT_DESCRIPTION));
    }

    @Test
    public void getNonExistingCompetExamResponse() {
        // Get the competExamResponse
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exam-responses/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

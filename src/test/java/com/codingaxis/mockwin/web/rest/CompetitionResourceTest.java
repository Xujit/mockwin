package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.Competition;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class CompetitionResourceTest {

    private static final TypeRef<Competition> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<Competition>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final Long DEFAULT_COUNTRY_ID = 1L;
    private static final Long UPDATED_COUNTRY_ID = 2L;

    private static final Long DEFAULT_CATEGORY_ID = 1L;
    private static final Long UPDATED_CATEGORY_ID = 2L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";



    String adminToken;

    Competition competition;

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
    public static Competition createEntity() {
        var competition = new Competition();
        competition.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        competition.countryId = DEFAULT_COUNTRY_ID;
        competition.categoryId = DEFAULT_CATEGORY_ID;
        competition.name = DEFAULT_NAME;
        competition.description = DEFAULT_DESCRIPTION;
        return competition;
    }

    @BeforeEach
    public void initTest() {
        competition = createEntity();
    }

    @Test
    public void createCompetition() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Competition
        competition = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competition)
            .when()
            .post("/api/competitions")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the Competition in the database
        var competitionList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competitionList).hasSize(databaseSizeBeforeCreate + 1);
        var testCompetition = competitionList.stream().filter(it -> competition.id.equals(it.id)).findFirst().get();
        assertThat(testCompetition.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testCompetition.countryId).isEqualTo(DEFAULT_COUNTRY_ID);
        assertThat(testCompetition.categoryId).isEqualTo(DEFAULT_CATEGORY_ID);
        assertThat(testCompetition.name).isEqualTo(DEFAULT_NAME);
        assertThat(testCompetition.description).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    public void createCompetitionWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Competition with an existing ID
        competition.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competition)
            .when()
            .post("/api/competitions")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Competition in the database
        var competitionList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competitionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        competition.name = null;

        // Create the Competition, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competition)
            .when()
            .post("/api/competitions")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Competition in the database
        var competitionList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competitionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateCompetition() {
        // Initialize the database
        competition = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competition)
            .when()
            .post("/api/competitions")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the competition
        var updatedCompetition = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions/{id}", competition.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the competition
        updatedCompetition.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedCompetition.countryId = UPDATED_COUNTRY_ID;
        updatedCompetition.categoryId = UPDATED_CATEGORY_ID;
        updatedCompetition.name = UPDATED_NAME;
        updatedCompetition.description = UPDATED_DESCRIPTION;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedCompetition)
            .when()
            .put("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the Competition in the database
        var competitionList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competitionList).hasSize(databaseSizeBeforeUpdate);
        var testCompetition = competitionList.stream().filter(it -> updatedCompetition.id.equals(it.id)).findFirst().get();
        assertThat(testCompetition.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testCompetition.countryId).isEqualTo(UPDATED_COUNTRY_ID);
        assertThat(testCompetition.categoryId).isEqualTo(UPDATED_CATEGORY_ID);
        assertThat(testCompetition.name).isEqualTo(UPDATED_NAME);
        assertThat(testCompetition.description).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    public void updateNonExistingCompetition() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
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
            .body(competition)
            .when()
            .put("/api/competitions")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Competition in the database
        var competitionList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competitionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCompetition() {
        // Initialize the database
        competition = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competition)
            .when()
            .post("/api/competitions")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the competition
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/competitions/{id}", competition.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var competitionList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competitionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllCompetitions() {
        // Initialize the database
        competition = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competition)
            .when()
            .post("/api/competitions")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the competitionList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(competition.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("countryId", hasItem(DEFAULT_COUNTRY_ID.intValue()))            .body("categoryId", hasItem(DEFAULT_CATEGORY_ID.intValue()))            .body("name", hasItem(DEFAULT_NAME))            .body("description", hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    public void getCompetition() {
        // Initialize the database
        competition = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competition)
            .when()
            .post("/api/competitions")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the competition
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/competitions/{id}", competition.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the competition
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions/{id}", competition.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(competition.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("countryId", is(DEFAULT_COUNTRY_ID.intValue()))
                .body("categoryId", is(DEFAULT_CATEGORY_ID.intValue()))
                .body("name", is(DEFAULT_NAME))
                .body("description", is(DEFAULT_DESCRIPTION));
    }

    @Test
    public void getNonExistingCompetition() {
        // Get the competition
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/competitions/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

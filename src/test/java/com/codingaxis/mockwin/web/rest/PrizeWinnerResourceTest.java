package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.PrizeWinner;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class PrizeWinnerResourceTest {

    private static final TypeRef<PrizeWinner> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<PrizeWinner>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PRIZE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PRIZE_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_POSITION = 1L;
    private static final Long UPDATED_POSITION = 2L;



    String adminToken;

    PrizeWinner prizeWinner;

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
    public static PrizeWinner createEntity() {
        var prizeWinner = new PrizeWinner();
        prizeWinner.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        prizeWinner.name = DEFAULT_NAME;
        prizeWinner.prizeName = DEFAULT_PRIZE_NAME;
        prizeWinner.position = DEFAULT_POSITION;
        return prizeWinner;
    }

    @BeforeEach
    public void initTest() {
        prizeWinner = createEntity();
    }

    @Test
    public void createPrizeWinner() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the PrizeWinner
        prizeWinner = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(prizeWinner)
            .when()
            .post("/api/prize-winners")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the PrizeWinner in the database
        var prizeWinnerList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(prizeWinnerList).hasSize(databaseSizeBeforeCreate + 1);
        var testPrizeWinner = prizeWinnerList.stream().filter(it -> prizeWinner.id.equals(it.id)).findFirst().get();
        assertThat(testPrizeWinner.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testPrizeWinner.name).isEqualTo(DEFAULT_NAME);
        assertThat(testPrizeWinner.prizeName).isEqualTo(DEFAULT_PRIZE_NAME);
        assertThat(testPrizeWinner.position).isEqualTo(DEFAULT_POSITION);
    }

    @Test
    public void createPrizeWinnerWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the PrizeWinner with an existing ID
        prizeWinner.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(prizeWinner)
            .when()
            .post("/api/prize-winners")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the PrizeWinner in the database
        var prizeWinnerList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(prizeWinnerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkPrizeNameIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        prizeWinner.prizeName = null;

        // Create the PrizeWinner, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(prizeWinner)
            .when()
            .post("/api/prize-winners")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the PrizeWinner in the database
        var prizeWinnerList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(prizeWinnerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updatePrizeWinner() {
        // Initialize the database
        prizeWinner = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(prizeWinner)
            .when()
            .post("/api/prize-winners")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the prizeWinner
        var updatedPrizeWinner = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners/{id}", prizeWinner.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the prizeWinner
        updatedPrizeWinner.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedPrizeWinner.name = UPDATED_NAME;
        updatedPrizeWinner.prizeName = UPDATED_PRIZE_NAME;
        updatedPrizeWinner.position = UPDATED_POSITION;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedPrizeWinner)
            .when()
            .put("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the PrizeWinner in the database
        var prizeWinnerList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(prizeWinnerList).hasSize(databaseSizeBeforeUpdate);
        var testPrizeWinner = prizeWinnerList.stream().filter(it -> updatedPrizeWinner.id.equals(it.id)).findFirst().get();
        assertThat(testPrizeWinner.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testPrizeWinner.name).isEqualTo(UPDATED_NAME);
        assertThat(testPrizeWinner.prizeName).isEqualTo(UPDATED_PRIZE_NAME);
        assertThat(testPrizeWinner.position).isEqualTo(UPDATED_POSITION);
    }

    @Test
    public void updateNonExistingPrizeWinner() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
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
            .body(prizeWinner)
            .when()
            .put("/api/prize-winners")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the PrizeWinner in the database
        var prizeWinnerList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(prizeWinnerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deletePrizeWinner() {
        // Initialize the database
        prizeWinner = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(prizeWinner)
            .when()
            .post("/api/prize-winners")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the prizeWinner
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/prize-winners/{id}", prizeWinner.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var prizeWinnerList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(prizeWinnerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllPrizeWinners() {
        // Initialize the database
        prizeWinner = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(prizeWinner)
            .when()
            .post("/api/prize-winners")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the prizeWinnerList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(prizeWinner.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("name", hasItem(DEFAULT_NAME))            .body("prizeName", hasItem(DEFAULT_PRIZE_NAME))            .body("position", hasItem(DEFAULT_POSITION.intValue()));
    }

    @Test
    public void getPrizeWinner() {
        // Initialize the database
        prizeWinner = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(prizeWinner)
            .when()
            .post("/api/prize-winners")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the prizeWinner
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/prize-winners/{id}", prizeWinner.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the prizeWinner
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners/{id}", prizeWinner.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(prizeWinner.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("name", is(DEFAULT_NAME))
                .body("prizeName", is(DEFAULT_PRIZE_NAME))
                .body("position", is(DEFAULT_POSITION.intValue()));
    }

    @Test
    public void getNonExistingPrizeWinner() {
        // Get the prizeWinner
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/prize-winners/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

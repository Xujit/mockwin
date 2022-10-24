package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.Options;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class OptionsResourceTest {

    private static final TypeRef<Options> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<Options>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final String DEFAULT_ANSWER = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_CORRECT = false;
    private static final Boolean UPDATED_CORRECT = true;



    String adminToken;

    Options options;

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
    public static Options createEntity() {
        var options = new Options();
        options.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        options.answer = DEFAULT_ANSWER;
        options.correct = DEFAULT_CORRECT;
        return options;
    }

    @BeforeEach
    public void initTest() {
        options = createEntity();
    }

    @Test
    public void createOptions() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Options
        options = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(options)
            .when()
            .post("/api/options")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the Options in the database
        var optionsList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(optionsList).hasSize(databaseSizeBeforeCreate + 1);
        var testOptions = optionsList.stream().filter(it -> options.id.equals(it.id)).findFirst().get();
        assertThat(testOptions.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testOptions.answer).isEqualTo(DEFAULT_ANSWER);
        assertThat(testOptions.correct).isEqualTo(DEFAULT_CORRECT);
    }

    @Test
    public void createOptionsWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Options with an existing ID
        options.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(options)
            .when()
            .post("/api/options")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Options in the database
        var optionsList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(optionsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkAnswerIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        options.answer = null;

        // Create the Options, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(options)
            .when()
            .post("/api/options")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Options in the database
        var optionsList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(optionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateOptions() {
        // Initialize the database
        options = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(options)
            .when()
            .post("/api/options")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the options
        var updatedOptions = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options/{id}", options.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the options
        updatedOptions.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedOptions.answer = UPDATED_ANSWER;
        updatedOptions.correct = UPDATED_CORRECT;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedOptions)
            .when()
            .put("/api/options")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the Options in the database
        var optionsList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
        var testOptions = optionsList.stream().filter(it -> updatedOptions.id.equals(it.id)).findFirst().get();
        assertThat(testOptions.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testOptions.answer).isEqualTo(UPDATED_ANSWER);
        assertThat(testOptions.correct).isEqualTo(UPDATED_CORRECT);
    }

    @Test
    public void updateNonExistingOptions() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
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
            .body(options)
            .when()
            .put("/api/options")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Options in the database
        var optionsList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteOptions() {
        // Initialize the database
        options = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(options)
            .when()
            .post("/api/options")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the options
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/options/{id}", options.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var optionsList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(optionsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllOptions() {
        // Initialize the database
        options = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(options)
            .when()
            .post("/api/options")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the optionsList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(options.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("answer", hasItem(DEFAULT_ANSWER))            .body("correct", hasItem(DEFAULT_CORRECT.booleanValue()));
    }

    @Test
    public void getOptions() {
        // Initialize the database
        options = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(options)
            .when()
            .post("/api/options")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the options
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/options/{id}", options.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the options
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options/{id}", options.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(options.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("answer", is(DEFAULT_ANSWER))
                .body("correct", is(DEFAULT_CORRECT.booleanValue()));
    }

    @Test
    public void getNonExistingOptions() {
        // Get the options
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/options/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

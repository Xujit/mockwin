package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.ContestSme;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class ContestSmeResourceTest {

    private static final TypeRef<ContestSme> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<ContestSme>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final Long DEFAULT_CONTEST_ID = 1L;
    private static final Long UPDATED_CONTEST_ID = 2L;

    private static final Long DEFAULT_NO_OF_MCQS = 1L;
    private static final Long UPDATED_NO_OF_MCQS = 2L;

    private static final Long DEFAULT_ASSIGNED_TO = 1L;
    private static final Long UPDATED_ASSIGNED_TO = 2L;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_INSTRUCTIONS = "AAAAAAAAAA";
    private static final String UPDATED_INSTRUCTIONS = "BBBBBBBBBB";

    private static final Long DEFAULT_SUPER_SME_ID = 1L;
    private static final Long UPDATED_SUPER_SME_ID = 2L;



    String adminToken;

    ContestSme contestSme;

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
    public static ContestSme createEntity() {
        var contestSme = new ContestSme();
        contestSme.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        contestSme.contestId = DEFAULT_CONTEST_ID;
        contestSme.noOfMcqs = DEFAULT_NO_OF_MCQS;
        contestSme.assignedTo = DEFAULT_ASSIGNED_TO;
        contestSme.status = DEFAULT_STATUS;
        contestSme.instructions = DEFAULT_INSTRUCTIONS;
        contestSme.superSMEId = DEFAULT_SUPER_SME_ID;
        return contestSme;
    }

    @BeforeEach
    public void initTest() {
        contestSme = createEntity();
    }

    @Test
    public void createContestSme() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ContestSme
        contestSme = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestSme)
            .when()
            .post("/api/contest-smes")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the ContestSme in the database
        var contestSmeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestSmeList).hasSize(databaseSizeBeforeCreate + 1);
        var testContestSme = contestSmeList.stream().filter(it -> contestSme.id.equals(it.id)).findFirst().get();
        assertThat(testContestSme.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testContestSme.contestId).isEqualTo(DEFAULT_CONTEST_ID);
        assertThat(testContestSme.noOfMcqs).isEqualTo(DEFAULT_NO_OF_MCQS);
        assertThat(testContestSme.assignedTo).isEqualTo(DEFAULT_ASSIGNED_TO);
        assertThat(testContestSme.status).isEqualTo(DEFAULT_STATUS);
        assertThat(testContestSme.instructions).isEqualTo(DEFAULT_INSTRUCTIONS);
        assertThat(testContestSme.superSMEId).isEqualTo(DEFAULT_SUPER_SME_ID);
    }

    @Test
    public void createContestSmeWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ContestSme with an existing ID
        contestSme.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestSme)
            .when()
            .post("/api/contest-smes")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ContestSme in the database
        var contestSmeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestSmeList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateContestSme() {
        // Initialize the database
        contestSme = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestSme)
            .when()
            .post("/api/contest-smes")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the contestSme
        var updatedContestSme = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes/{id}", contestSme.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the contestSme
        updatedContestSme.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedContestSme.contestId = UPDATED_CONTEST_ID;
        updatedContestSme.noOfMcqs = UPDATED_NO_OF_MCQS;
        updatedContestSme.assignedTo = UPDATED_ASSIGNED_TO;
        updatedContestSme.status = UPDATED_STATUS;
        updatedContestSme.instructions = UPDATED_INSTRUCTIONS;
        updatedContestSme.superSMEId = UPDATED_SUPER_SME_ID;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedContestSme)
            .when()
            .put("/api/contest-smes")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the ContestSme in the database
        var contestSmeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestSmeList).hasSize(databaseSizeBeforeUpdate);
        var testContestSme = contestSmeList.stream().filter(it -> updatedContestSme.id.equals(it.id)).findFirst().get();
        assertThat(testContestSme.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testContestSme.contestId).isEqualTo(UPDATED_CONTEST_ID);
        assertThat(testContestSme.noOfMcqs).isEqualTo(UPDATED_NO_OF_MCQS);
        assertThat(testContestSme.assignedTo).isEqualTo(UPDATED_ASSIGNED_TO);
        assertThat(testContestSme.status).isEqualTo(UPDATED_STATUS);
        assertThat(testContestSme.instructions).isEqualTo(UPDATED_INSTRUCTIONS);
        assertThat(testContestSme.superSMEId).isEqualTo(UPDATED_SUPER_SME_ID);
    }

    @Test
    public void updateNonExistingContestSme() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes")
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
            .body(contestSme)
            .when()
            .put("/api/contest-smes")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ContestSme in the database
        var contestSmeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestSmeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteContestSme() {
        // Initialize the database
        contestSme = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestSme)
            .when()
            .post("/api/contest-smes")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the contestSme
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/contest-smes/{id}", contestSme.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var contestSmeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestSmeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllContestSmes() {
        // Initialize the database
        contestSme = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestSme)
            .when()
            .post("/api/contest-smes")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the contestSmeList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(contestSme.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("contestId", hasItem(DEFAULT_CONTEST_ID.intValue()))            .body("noOfMcqs", hasItem(DEFAULT_NO_OF_MCQS.intValue()))            .body("assignedTo", hasItem(DEFAULT_ASSIGNED_TO.intValue()))            .body("status", hasItem(DEFAULT_STATUS))            .body("instructions", hasItem(DEFAULT_INSTRUCTIONS))            .body("superSMEId", hasItem(DEFAULT_SUPER_SME_ID.intValue()));
    }

    @Test
    public void getContestSme() {
        // Initialize the database
        contestSme = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestSme)
            .when()
            .post("/api/contest-smes")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the contestSme
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/contest-smes/{id}", contestSme.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the contestSme
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes/{id}", contestSme.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(contestSme.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("contestId", is(DEFAULT_CONTEST_ID.intValue()))
                .body("noOfMcqs", is(DEFAULT_NO_OF_MCQS.intValue()))
                .body("assignedTo", is(DEFAULT_ASSIGNED_TO.intValue()))
                .body("status", is(DEFAULT_STATUS))
                .body("instructions", is(DEFAULT_INSTRUCTIONS))
                .body("superSMEId", is(DEFAULT_SUPER_SME_ID.intValue()));
    }

    @Test
    public void getNonExistingContestSme() {
        // Get the contestSme
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-smes/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

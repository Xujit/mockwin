package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.ContestStatusReponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    import java.time.LocalDate;
import java.time.ZoneId;

@QuarkusTest
public class ContestStatusReponseResourceTest {

    private static final TypeRef<ContestStatusReponse> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<ContestStatusReponse>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_CONTEST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CONTEST_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_CONTEST_ID = 1L;
    private static final Long UPDATED_CONTEST_ID = 2L;



    String adminToken;

    ContestStatusReponse contestStatusReponse;

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
    public static ContestStatusReponse createEntity() {
        var contestStatusReponse = new ContestStatusReponse();
        contestStatusReponse.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        contestStatusReponse.startDate = DEFAULT_START_DATE;
        contestStatusReponse.contestName = DEFAULT_CONTEST_NAME;
        contestStatusReponse.contestId = DEFAULT_CONTEST_ID;
        return contestStatusReponse;
    }

    @BeforeEach
    public void initTest() {
        contestStatusReponse = createEntity();
    }

    @Test
    public void createContestStatusReponse() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ContestStatusReponse
        contestStatusReponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestStatusReponse)
            .when()
            .post("/api/contest-status-reponses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the ContestStatusReponse in the database
        var contestStatusReponseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestStatusReponseList).hasSize(databaseSizeBeforeCreate + 1);
        var testContestStatusReponse = contestStatusReponseList.stream().filter(it -> contestStatusReponse.id.equals(it.id)).findFirst().get();
        assertThat(testContestStatusReponse.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testContestStatusReponse.startDate).isEqualTo(DEFAULT_START_DATE);
        assertThat(testContestStatusReponse.contestName).isEqualTo(DEFAULT_CONTEST_NAME);
        assertThat(testContestStatusReponse.contestId).isEqualTo(DEFAULT_CONTEST_ID);
    }

    @Test
    public void createContestStatusReponseWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ContestStatusReponse with an existing ID
        contestStatusReponse.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestStatusReponse)
            .when()
            .post("/api/contest-status-reponses")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ContestStatusReponse in the database
        var contestStatusReponseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestStatusReponseList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateContestStatusReponse() {
        // Initialize the database
        contestStatusReponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestStatusReponse)
            .when()
            .post("/api/contest-status-reponses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the contestStatusReponse
        var updatedContestStatusReponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses/{id}", contestStatusReponse.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the contestStatusReponse
        updatedContestStatusReponse.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedContestStatusReponse.startDate = UPDATED_START_DATE;
        updatedContestStatusReponse.contestName = UPDATED_CONTEST_NAME;
        updatedContestStatusReponse.contestId = UPDATED_CONTEST_ID;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedContestStatusReponse)
            .when()
            .put("/api/contest-status-reponses")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the ContestStatusReponse in the database
        var contestStatusReponseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestStatusReponseList).hasSize(databaseSizeBeforeUpdate);
        var testContestStatusReponse = contestStatusReponseList.stream().filter(it -> updatedContestStatusReponse.id.equals(it.id)).findFirst().get();
        assertThat(testContestStatusReponse.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testContestStatusReponse.startDate).isEqualTo(UPDATED_START_DATE);
        assertThat(testContestStatusReponse.contestName).isEqualTo(UPDATED_CONTEST_NAME);
        assertThat(testContestStatusReponse.contestId).isEqualTo(UPDATED_CONTEST_ID);
    }

    @Test
    public void updateNonExistingContestStatusReponse() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses")
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
            .body(contestStatusReponse)
            .when()
            .put("/api/contest-status-reponses")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ContestStatusReponse in the database
        var contestStatusReponseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestStatusReponseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteContestStatusReponse() {
        // Initialize the database
        contestStatusReponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestStatusReponse)
            .when()
            .post("/api/contest-status-reponses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the contestStatusReponse
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/contest-status-reponses/{id}", contestStatusReponse.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var contestStatusReponseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestStatusReponseList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllContestStatusReponses() {
        // Initialize the database
        contestStatusReponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestStatusReponse)
            .when()
            .post("/api/contest-status-reponses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the contestStatusReponseList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(contestStatusReponse.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("startDate", hasItem(TestUtil.formatDateTime(DEFAULT_START_DATE)))            .body("contestName", hasItem(DEFAULT_CONTEST_NAME))            .body("contestId", hasItem(DEFAULT_CONTEST_ID.intValue()));
    }

    @Test
    public void getContestStatusReponse() {
        // Initialize the database
        contestStatusReponse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestStatusReponse)
            .when()
            .post("/api/contest-status-reponses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the contestStatusReponse
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/contest-status-reponses/{id}", contestStatusReponse.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the contestStatusReponse
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses/{id}", contestStatusReponse.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(contestStatusReponse.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("startDate", is(TestUtil.formatDateTime(DEFAULT_START_DATE)))
                .body("contestName", is(DEFAULT_CONTEST_NAME))
                .body("contestId", is(DEFAULT_CONTEST_ID.intValue()));
    }

    @Test
    public void getNonExistingContestStatusReponse() {
        // Get the contestStatusReponse
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-status-reponses/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.CompetExam;
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
public class CompetExamResourceTest {

    private static final TypeRef<CompetExam> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<CompetExam>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final Long DEFAULT_COMPET_EXAMS_ID = 1L;
    private static final Long UPDATED_COMPET_EXAMS_ID = 2L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_LAST_UPDATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_UPDATED = LocalDate.now(ZoneId.systemDefault());



    String adminToken;

    CompetExam competExam;

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
    public static CompetExam createEntity() {
        var competExam = new CompetExam();
        competExam.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        competExam.competExamsId = DEFAULT_COMPET_EXAMS_ID;
        competExam.name = DEFAULT_NAME;
        competExam.description = DEFAULT_DESCRIPTION;
        competExam.status = DEFAULT_STATUS;
        competExam.createdDate = DEFAULT_CREATED_DATE;
        competExam.lastUpdated = DEFAULT_LAST_UPDATED;
        return competExam;
    }

    @BeforeEach
    public void initTest() {
        competExam = createEntity();
    }

    @Test
    public void createCompetExam() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the CompetExam
        competExam = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExam)
            .when()
            .post("/api/compet-exams")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the CompetExam in the database
        var competExamList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamList).hasSize(databaseSizeBeforeCreate + 1);
        var testCompetExam = competExamList.stream().filter(it -> competExam.id.equals(it.id)).findFirst().get();
        assertThat(testCompetExam.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testCompetExam.competExamsId).isEqualTo(DEFAULT_COMPET_EXAMS_ID);
        assertThat(testCompetExam.name).isEqualTo(DEFAULT_NAME);
        assertThat(testCompetExam.description).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCompetExam.status).isEqualTo(DEFAULT_STATUS);
        assertThat(testCompetExam.createdDate).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testCompetExam.lastUpdated).isEqualTo(DEFAULT_LAST_UPDATED);
    }

    @Test
    public void createCompetExamWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the CompetExam with an existing ID
        competExam.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExam)
            .when()
            .post("/api/compet-exams")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CompetExam in the database
        var competExamList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        competExam.name = null;

        // Create the CompetExam, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExam)
            .when()
            .post("/api/compet-exams")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CompetExam in the database
        var competExamList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateCompetExam() {
        // Initialize the database
        competExam = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExam)
            .when()
            .post("/api/compet-exams")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the competExam
        var updatedCompetExam = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams/{id}", competExam.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the competExam
        updatedCompetExam.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedCompetExam.competExamsId = UPDATED_COMPET_EXAMS_ID;
        updatedCompetExam.name = UPDATED_NAME;
        updatedCompetExam.description = UPDATED_DESCRIPTION;
        updatedCompetExam.status = UPDATED_STATUS;
        updatedCompetExam.createdDate = UPDATED_CREATED_DATE;
        updatedCompetExam.lastUpdated = UPDATED_LAST_UPDATED;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedCompetExam)
            .when()
            .put("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the CompetExam in the database
        var competExamList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamList).hasSize(databaseSizeBeforeUpdate);
        var testCompetExam = competExamList.stream().filter(it -> updatedCompetExam.id.equals(it.id)).findFirst().get();
        assertThat(testCompetExam.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testCompetExam.competExamsId).isEqualTo(UPDATED_COMPET_EXAMS_ID);
        assertThat(testCompetExam.name).isEqualTo(UPDATED_NAME);
        assertThat(testCompetExam.description).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCompetExam.status).isEqualTo(UPDATED_STATUS);
        assertThat(testCompetExam.createdDate).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testCompetExam.lastUpdated).isEqualTo(UPDATED_LAST_UPDATED);
    }

    @Test
    public void updateNonExistingCompetExam() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
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
            .body(competExam)
            .when()
            .put("/api/compet-exams")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CompetExam in the database
        var competExamList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCompetExam() {
        // Initialize the database
        competExam = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExam)
            .when()
            .post("/api/compet-exams")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the competExam
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/compet-exams/{id}", competExam.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var competExamList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(competExamList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllCompetExams() {
        // Initialize the database
        competExam = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExam)
            .when()
            .post("/api/compet-exams")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the competExamList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(competExam.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("competExamsId", hasItem(DEFAULT_COMPET_EXAMS_ID.intValue()))            .body("name", hasItem(DEFAULT_NAME))            .body("description", hasItem(DEFAULT_DESCRIPTION))            .body("status", hasItem(DEFAULT_STATUS.intValue()))            .body("createdDate", hasItem(TestUtil.formatDateTime(DEFAULT_CREATED_DATE)))            .body("lastUpdated", hasItem(TestUtil.formatDateTime(DEFAULT_LAST_UPDATED)));
    }

    @Test
    public void getCompetExam() {
        // Initialize the database
        competExam = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(competExam)
            .when()
            .post("/api/compet-exams")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the competExam
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/compet-exams/{id}", competExam.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the competExam
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams/{id}", competExam.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(competExam.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("competExamsId", is(DEFAULT_COMPET_EXAMS_ID.intValue()))
                .body("name", is(DEFAULT_NAME))
                .body("description", is(DEFAULT_DESCRIPTION))
                .body("status", is(DEFAULT_STATUS.intValue()))
                .body("createdDate", is(TestUtil.formatDateTime(DEFAULT_CREATED_DATE)))
                .body("lastUpdated", is(TestUtil.formatDateTime(DEFAULT_LAST_UPDATED)));
    }

    @Test
    public void getNonExistingCompetExam() {
        // Get the competExam
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/compet-exams/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

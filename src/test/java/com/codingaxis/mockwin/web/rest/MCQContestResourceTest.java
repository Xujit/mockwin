package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.MCQContest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class MCQContestResourceTest {

    private static final TypeRef<MCQContest> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<MCQContest>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final Long DEFAULT_CONTEST_ID = 1L;
    private static final Long UPDATED_CONTEST_ID = 2L;

    private static final Long DEFAULT_NO_OF_MCQS = 1L;
    private static final Long UPDATED_NO_OF_MCQS = 2L;

    private static final Long DEFAULT_ASSIGNED_TO = 1L;
    private static final Long UPDATED_ASSIGNED_TO = 2L;

    private static final Long DEFAULT_USER_TYPE_ID = 1L;
    private static final Long UPDATED_USER_TYPE_ID = 2L;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Long DEFAULT_SUPER_SME_ID = 1L;
    private static final Long UPDATED_SUPER_SME_ID = 2L;



    String adminToken;

    MCQContest mCQContest;

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
    public static MCQContest createEntity() {
        var mCQContest = new MCQContest();
        mCQContest.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        mCQContest.contestId = DEFAULT_CONTEST_ID;
        mCQContest.noOfMcqs = DEFAULT_NO_OF_MCQS;
        mCQContest.assignedTo = DEFAULT_ASSIGNED_TO;
        mCQContest.userTypeId = DEFAULT_USER_TYPE_ID;
        mCQContest.status = DEFAULT_STATUS;
        mCQContest.fullName = DEFAULT_FULL_NAME;
        mCQContest.comments = DEFAULT_COMMENTS;
        mCQContest.reason = DEFAULT_REASON;
        mCQContest.superSmeId = DEFAULT_SUPER_SME_ID;
        return mCQContest;
    }

    @BeforeEach
    public void initTest() {
        mCQContest = createEntity();
    }

    @Test
    public void createMCQContest() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the MCQContest
        mCQContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContest)
            .when()
            .post("/api/mcq-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the MCQContest in the database
        var mCQContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mCQContestList).hasSize(databaseSizeBeforeCreate + 1);
        var testMCQContest = mCQContestList.stream().filter(it -> mCQContest.id.equals(it.id)).findFirst().get();
        assertThat(testMCQContest.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testMCQContest.contestId).isEqualTo(DEFAULT_CONTEST_ID);
        assertThat(testMCQContest.noOfMcqs).isEqualTo(DEFAULT_NO_OF_MCQS);
        assertThat(testMCQContest.assignedTo).isEqualTo(DEFAULT_ASSIGNED_TO);
        assertThat(testMCQContest.userTypeId).isEqualTo(DEFAULT_USER_TYPE_ID);
        assertThat(testMCQContest.status).isEqualTo(DEFAULT_STATUS);
        assertThat(testMCQContest.fullName).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testMCQContest.comments).isEqualTo(DEFAULT_COMMENTS);
        assertThat(testMCQContest.reason).isEqualTo(DEFAULT_REASON);
        assertThat(testMCQContest.superSmeId).isEqualTo(DEFAULT_SUPER_SME_ID);
    }

    @Test
    public void createMCQContestWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the MCQContest with an existing ID
        mCQContest.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContest)
            .when()
            .post("/api/mcq-contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the MCQContest in the database
        var mCQContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mCQContestList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateMCQContest() {
        // Initialize the database
        mCQContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContest)
            .when()
            .post("/api/mcq-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the mCQContest
        var updatedMCQContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests/{id}", mCQContest.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the mCQContest
        updatedMCQContest.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedMCQContest.contestId = UPDATED_CONTEST_ID;
        updatedMCQContest.noOfMcqs = UPDATED_NO_OF_MCQS;
        updatedMCQContest.assignedTo = UPDATED_ASSIGNED_TO;
        updatedMCQContest.userTypeId = UPDATED_USER_TYPE_ID;
        updatedMCQContest.status = UPDATED_STATUS;
        updatedMCQContest.fullName = UPDATED_FULL_NAME;
        updatedMCQContest.comments = UPDATED_COMMENTS;
        updatedMCQContest.reason = UPDATED_REASON;
        updatedMCQContest.superSmeId = UPDATED_SUPER_SME_ID;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedMCQContest)
            .when()
            .put("/api/mcq-contests")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the MCQContest in the database
        var mCQContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mCQContestList).hasSize(databaseSizeBeforeUpdate);
        var testMCQContest = mCQContestList.stream().filter(it -> updatedMCQContest.id.equals(it.id)).findFirst().get();
        assertThat(testMCQContest.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testMCQContest.contestId).isEqualTo(UPDATED_CONTEST_ID);
        assertThat(testMCQContest.noOfMcqs).isEqualTo(UPDATED_NO_OF_MCQS);
        assertThat(testMCQContest.assignedTo).isEqualTo(UPDATED_ASSIGNED_TO);
        assertThat(testMCQContest.userTypeId).isEqualTo(UPDATED_USER_TYPE_ID);
        assertThat(testMCQContest.status).isEqualTo(UPDATED_STATUS);
        assertThat(testMCQContest.fullName).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testMCQContest.comments).isEqualTo(UPDATED_COMMENTS);
        assertThat(testMCQContest.reason).isEqualTo(UPDATED_REASON);
        assertThat(testMCQContest.superSmeId).isEqualTo(UPDATED_SUPER_SME_ID);
    }

    @Test
    public void updateNonExistingMCQContest() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests")
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
            .body(mCQContest)
            .when()
            .put("/api/mcq-contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the MCQContest in the database
        var mCQContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mCQContestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteMCQContest() {
        // Initialize the database
        mCQContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContest)
            .when()
            .post("/api/mcq-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the mCQContest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/mcq-contests/{id}", mCQContest.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var mCQContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mCQContestList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllMCQContests() {
        // Initialize the database
        mCQContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContest)
            .when()
            .post("/api/mcq-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the mCQContestList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(mCQContest.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("contestId", hasItem(DEFAULT_CONTEST_ID.intValue()))            .body("noOfMcqs", hasItem(DEFAULT_NO_OF_MCQS.intValue()))            .body("assignedTo", hasItem(DEFAULT_ASSIGNED_TO.intValue()))            .body("userTypeId", hasItem(DEFAULT_USER_TYPE_ID.intValue()))            .body("status", hasItem(DEFAULT_STATUS))            .body("fullName", hasItem(DEFAULT_FULL_NAME))            .body("comments", hasItem(DEFAULT_COMMENTS))            .body("reason", hasItem(DEFAULT_REASON))            .body("superSmeId", hasItem(DEFAULT_SUPER_SME_ID.intValue()));
    }

    @Test
    public void getMCQContest() {
        // Initialize the database
        mCQContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContest)
            .when()
            .post("/api/mcq-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the mCQContest
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/mcq-contests/{id}", mCQContest.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the mCQContest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests/{id}", mCQContest.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(mCQContest.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("contestId", is(DEFAULT_CONTEST_ID.intValue()))
                .body("noOfMcqs", is(DEFAULT_NO_OF_MCQS.intValue()))
                .body("assignedTo", is(DEFAULT_ASSIGNED_TO.intValue()))
                .body("userTypeId", is(DEFAULT_USER_TYPE_ID.intValue()))
                .body("status", is(DEFAULT_STATUS))
                .body("fullName", is(DEFAULT_FULL_NAME))
                .body("comments", is(DEFAULT_COMMENTS))
                .body("reason", is(DEFAULT_REASON))
                .body("superSmeId", is(DEFAULT_SUPER_SME_ID.intValue()));
    }

    @Test
    public void getNonExistingMCQContest() {
        // Get the mCQContest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contests/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

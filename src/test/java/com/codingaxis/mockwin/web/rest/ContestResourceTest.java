package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.Contest;
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
public class ContestResourceTest {

    private static final TypeRef<Contest> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<Contest>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_COMMENCE_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_COMMENCE_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_FINISH_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FINISH_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_DURATION = 1L;
    private static final Long UPDATED_DURATION = 2L;

    private static final String DEFAULT_PRIZES = "AAAAAAAAAA";
    private static final String UPDATED_PRIZES = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_REGION = "AAAAAAAAAA";
    private static final String UPDATED_REGION = "BBBBBBBBBB";

    private static final Long DEFAULT_NOOFMCQS = 1L;
    private static final Long UPDATED_NOOFMCQS = 2L;

    private static final String DEFAULT_ELIGIBILITY_CRITERIA = "AAAAAAAAAA";
    private static final String UPDATED_ELIGIBILITY_CRITERIA = "BBBBBBBBBB";

    private static final String DEFAULT_WINNER_SELECTION = "AAAAAAAAAA";
    private static final String UPDATED_WINNER_SELECTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_RECURRING = false;
    private static final Boolean UPDATED_RECURRING = true;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_LAST_UPDATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_UPDATED = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_COMPLETED = false;
    private static final Boolean UPDATED_COMPLETED = true;

    private static final Long DEFAULT_CREATED_BY = 1L;
    private static final Long UPDATED_CREATED_BY = 2L;

    private static final String DEFAULT_VISIBILITY = "AAAAAAAAAA";
    private static final String UPDATED_VISIBILITY = "BBBBBBBBBB";

    private static final String DEFAULT_SPONSORED_BY = "AAAAAAAAAA";
    private static final String UPDATED_SPONSORED_BY = "BBBBBBBBBB";

    private static final Long DEFAULT_FILE_ID = 1L;
    private static final Long UPDATED_FILE_ID = 2L;



    String adminToken;

    Contest contest;

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
    public static Contest createEntity() {
        var contest = new Contest();
        contest.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        contest.name = DEFAULT_NAME;
        contest.description = DEFAULT_DESCRIPTION;
        contest.type = DEFAULT_TYPE;
        contest.startDateTime = DEFAULT_START_DATE_TIME;
        contest.endDateTime = DEFAULT_END_DATE_TIME;
        contest.commenceTime = DEFAULT_COMMENCE_TIME;
        contest.finishTime = DEFAULT_FINISH_TIME;
        contest.duration = DEFAULT_DURATION;
        contest.prizes = DEFAULT_PRIZES;
        contest.state = DEFAULT_STATE;
        contest.region = DEFAULT_REGION;
        contest.noofmcqs = DEFAULT_NOOFMCQS;
        contest.eligibilityCriteria = DEFAULT_ELIGIBILITY_CRITERIA;
        contest.winnerSelection = DEFAULT_WINNER_SELECTION;
        contest.recurring = DEFAULT_RECURRING;
        contest.status = DEFAULT_STATUS;
        contest.created = DEFAULT_CREATED;
        contest.lastUpdated = DEFAULT_LAST_UPDATED;
        contest.completed = DEFAULT_COMPLETED;
        contest.createdBy = DEFAULT_CREATED_BY;
        contest.visibility = DEFAULT_VISIBILITY;
        contest.sponsoredBy = DEFAULT_SPONSORED_BY;
        contest.fileId = DEFAULT_FILE_ID;
        return contest;
    }

    @BeforeEach
    public void initTest() {
        contest = createEntity();
    }

    @Test
    public void createContest() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Contest
        contest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contest)
            .when()
            .post("/api/contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the Contest in the database
        var contestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestList).hasSize(databaseSizeBeforeCreate + 1);
        var testContest = contestList.stream().filter(it -> contest.id.equals(it.id)).findFirst().get();
        assertThat(testContest.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testContest.name).isEqualTo(DEFAULT_NAME);
        assertThat(testContest.description).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testContest.type).isEqualTo(DEFAULT_TYPE);
        assertThat(testContest.startDateTime).isEqualTo(DEFAULT_START_DATE_TIME);
        assertThat(testContest.endDateTime).isEqualTo(DEFAULT_END_DATE_TIME);
        assertThat(testContest.commenceTime).isEqualTo(DEFAULT_COMMENCE_TIME);
        assertThat(testContest.finishTime).isEqualTo(DEFAULT_FINISH_TIME);
        assertThat(testContest.duration).isEqualTo(DEFAULT_DURATION);
        assertThat(testContest.prizes).isEqualTo(DEFAULT_PRIZES);
        assertThat(testContest.state).isEqualTo(DEFAULT_STATE);
        assertThat(testContest.region).isEqualTo(DEFAULT_REGION);
        assertThat(testContest.noofmcqs).isEqualTo(DEFAULT_NOOFMCQS);
        assertThat(testContest.eligibilityCriteria).isEqualTo(DEFAULT_ELIGIBILITY_CRITERIA);
        assertThat(testContest.winnerSelection).isEqualTo(DEFAULT_WINNER_SELECTION);
        assertThat(testContest.recurring).isEqualTo(DEFAULT_RECURRING);
        assertThat(testContest.status).isEqualTo(DEFAULT_STATUS);
        assertThat(testContest.created).isEqualTo(DEFAULT_CREATED);
        assertThat(testContest.lastUpdated).isEqualTo(DEFAULT_LAST_UPDATED);
        assertThat(testContest.completed).isEqualTo(DEFAULT_COMPLETED);
        assertThat(testContest.createdBy).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testContest.visibility).isEqualTo(DEFAULT_VISIBILITY);
        assertThat(testContest.sponsoredBy).isEqualTo(DEFAULT_SPONSORED_BY);
        assertThat(testContest.fileId).isEqualTo(DEFAULT_FILE_ID);
    }

    @Test
    public void createContestWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Contest with an existing ID
        contest.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contest)
            .when()
            .post("/api/contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Contest in the database
        var contestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateContest() {
        // Initialize the database
        contest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contest)
            .when()
            .post("/api/contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the contest
        var updatedContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests/{id}", contest.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the contest
        updatedContest.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedContest.name = UPDATED_NAME;
        updatedContest.description = UPDATED_DESCRIPTION;
        updatedContest.type = UPDATED_TYPE;
        updatedContest.startDateTime = UPDATED_START_DATE_TIME;
        updatedContest.endDateTime = UPDATED_END_DATE_TIME;
        updatedContest.commenceTime = UPDATED_COMMENCE_TIME;
        updatedContest.finishTime = UPDATED_FINISH_TIME;
        updatedContest.duration = UPDATED_DURATION;
        updatedContest.prizes = UPDATED_PRIZES;
        updatedContest.state = UPDATED_STATE;
        updatedContest.region = UPDATED_REGION;
        updatedContest.noofmcqs = UPDATED_NOOFMCQS;
        updatedContest.eligibilityCriteria = UPDATED_ELIGIBILITY_CRITERIA;
        updatedContest.winnerSelection = UPDATED_WINNER_SELECTION;
        updatedContest.recurring = UPDATED_RECURRING;
        updatedContest.status = UPDATED_STATUS;
        updatedContest.created = UPDATED_CREATED;
        updatedContest.lastUpdated = UPDATED_LAST_UPDATED;
        updatedContest.completed = UPDATED_COMPLETED;
        updatedContest.createdBy = UPDATED_CREATED_BY;
        updatedContest.visibility = UPDATED_VISIBILITY;
        updatedContest.sponsoredBy = UPDATED_SPONSORED_BY;
        updatedContest.fileId = UPDATED_FILE_ID;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedContest)
            .when()
            .put("/api/contests")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the Contest in the database
        var contestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestList).hasSize(databaseSizeBeforeUpdate);
        var testContest = contestList.stream().filter(it -> updatedContest.id.equals(it.id)).findFirst().get();
        assertThat(testContest.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testContest.name).isEqualTo(UPDATED_NAME);
        assertThat(testContest.description).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testContest.type).isEqualTo(UPDATED_TYPE);
        assertThat(testContest.startDateTime).isEqualTo(UPDATED_START_DATE_TIME);
        assertThat(testContest.endDateTime).isEqualTo(UPDATED_END_DATE_TIME);
        assertThat(testContest.commenceTime).isEqualTo(UPDATED_COMMENCE_TIME);
        assertThat(testContest.finishTime).isEqualTo(UPDATED_FINISH_TIME);
        assertThat(testContest.duration).isEqualTo(UPDATED_DURATION);
        assertThat(testContest.prizes).isEqualTo(UPDATED_PRIZES);
        assertThat(testContest.state).isEqualTo(UPDATED_STATE);
        assertThat(testContest.region).isEqualTo(UPDATED_REGION);
        assertThat(testContest.noofmcqs).isEqualTo(UPDATED_NOOFMCQS);
        assertThat(testContest.eligibilityCriteria).isEqualTo(UPDATED_ELIGIBILITY_CRITERIA);
        assertThat(testContest.winnerSelection).isEqualTo(UPDATED_WINNER_SELECTION);
        assertThat(testContest.recurring).isEqualTo(UPDATED_RECURRING);
        assertThat(testContest.status).isEqualTo(UPDATED_STATUS);
        assertThat(testContest.created).isEqualTo(UPDATED_CREATED);
        assertThat(testContest.lastUpdated).isEqualTo(UPDATED_LAST_UPDATED);
        assertThat(testContest.completed).isEqualTo(UPDATED_COMPLETED);
        assertThat(testContest.createdBy).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testContest.visibility).isEqualTo(UPDATED_VISIBILITY);
        assertThat(testContest.sponsoredBy).isEqualTo(UPDATED_SPONSORED_BY);
        assertThat(testContest.fileId).isEqualTo(UPDATED_FILE_ID);
    }

    @Test
    public void updateNonExistingContest() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests")
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
            .body(contest)
            .when()
            .put("/api/contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Contest in the database
        var contestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteContest() {
        // Initialize the database
        contest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contest)
            .when()
            .post("/api/contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the contest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/contests/{id}", contest.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var contestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllContests() {
        // Initialize the database
        contest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contest)
            .when()
            .post("/api/contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the contestList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(contest.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("name", hasItem(DEFAULT_NAME))            .body("description", hasItem(DEFAULT_DESCRIPTION))            .body("type", hasItem(DEFAULT_TYPE))            .body("startDateTime", hasItem(TestUtil.formatDateTime(DEFAULT_START_DATE_TIME)))            .body("endDateTime", hasItem(TestUtil.formatDateTime(DEFAULT_END_DATE_TIME)))            .body("commenceTime", hasItem(TestUtil.formatDateTime(DEFAULT_COMMENCE_TIME)))            .body("finishTime", hasItem(TestUtil.formatDateTime(DEFAULT_FINISH_TIME)))            .body("duration", hasItem(DEFAULT_DURATION.intValue()))            .body("prizes", hasItem(DEFAULT_PRIZES))            .body("state", hasItem(DEFAULT_STATE))            .body("region", hasItem(DEFAULT_REGION))            .body("noofmcqs", hasItem(DEFAULT_NOOFMCQS.intValue()))            .body("eligibilityCriteria", hasItem(DEFAULT_ELIGIBILITY_CRITERIA))            .body("winnerSelection", hasItem(DEFAULT_WINNER_SELECTION))            .body("recurring", hasItem(DEFAULT_RECURRING.booleanValue()))            .body("status", hasItem(DEFAULT_STATUS))            .body("created", hasItem(TestUtil.formatDateTime(DEFAULT_CREATED)))            .body("lastUpdated", hasItem(TestUtil.formatDateTime(DEFAULT_LAST_UPDATED)))            .body("completed", hasItem(DEFAULT_COMPLETED.booleanValue()))            .body("createdBy", hasItem(DEFAULT_CREATED_BY.intValue()))            .body("visibility", hasItem(DEFAULT_VISIBILITY))            .body("sponsoredBy", hasItem(DEFAULT_SPONSORED_BY))            .body("fileId", hasItem(DEFAULT_FILE_ID.intValue()));
    }

    @Test
    public void getContest() {
        // Initialize the database
        contest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contest)
            .when()
            .post("/api/contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the contest
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/contests/{id}", contest.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the contest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests/{id}", contest.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(contest.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("name", is(DEFAULT_NAME))
                .body("description", is(DEFAULT_DESCRIPTION))
                .body("type", is(DEFAULT_TYPE))
                .body("startDateTime", is(TestUtil.formatDateTime(DEFAULT_START_DATE_TIME)))
                .body("endDateTime", is(TestUtil.formatDateTime(DEFAULT_END_DATE_TIME)))
                .body("commenceTime", is(TestUtil.formatDateTime(DEFAULT_COMMENCE_TIME)))
                .body("finishTime", is(TestUtil.formatDateTime(DEFAULT_FINISH_TIME)))
                .body("duration", is(DEFAULT_DURATION.intValue()))
                .body("prizes", is(DEFAULT_PRIZES))
                .body("state", is(DEFAULT_STATE))
                .body("region", is(DEFAULT_REGION))
                .body("noofmcqs", is(DEFAULT_NOOFMCQS.intValue()))
                .body("eligibilityCriteria", is(DEFAULT_ELIGIBILITY_CRITERIA))
                .body("winnerSelection", is(DEFAULT_WINNER_SELECTION))
                .body("recurring", is(DEFAULT_RECURRING.booleanValue()))
                .body("status", is(DEFAULT_STATUS))
                .body("created", is(TestUtil.formatDateTime(DEFAULT_CREATED)))
                .body("lastUpdated", is(TestUtil.formatDateTime(DEFAULT_LAST_UPDATED)))
                .body("completed", is(DEFAULT_COMPLETED.booleanValue()))
                .body("createdBy", is(DEFAULT_CREATED_BY.intValue()))
                .body("visibility", is(DEFAULT_VISIBILITY))
                .body("sponsoredBy", is(DEFAULT_SPONSORED_BY))
                .body("fileId", is(DEFAULT_FILE_ID.intValue()));
    }

    @Test
    public void getNonExistingContest() {
        // Get the contest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contests/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

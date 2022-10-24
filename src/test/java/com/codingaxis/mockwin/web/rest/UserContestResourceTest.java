package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.UserContest;
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
public class UserContestResourceTest {

    private static final TypeRef<UserContest> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<UserContest>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_CONTEST_ID = 1L;
    private static final Long UPDATED_CONTEST_ID = 2L;

    private static final Long DEFAULT_RANK = 1L;
    private static final Long UPDATED_RANK = 2L;

    private static final Long DEFAULT_SCORE = 1L;
    private static final Long UPDATED_SCORE = 2L;

    private static final LocalDate DEFAULT_LAST_UPDATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_UPDATED = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_COMPLETED = false;
    private static final Boolean UPDATED_COMPLETED = true;

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;



    String adminToken;

    UserContest userContest;

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
    public static UserContest createEntity() {
        var userContest = new UserContest();
        userContest.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        userContest.userId = DEFAULT_USER_ID;
        userContest.contestId = DEFAULT_CONTEST_ID;
        userContest.rank = DEFAULT_RANK;
        userContest.score = DEFAULT_SCORE;
        userContest.lastUpdated = DEFAULT_LAST_UPDATED;
        userContest.created = DEFAULT_CREATED;
        userContest.completed = DEFAULT_COMPLETED;
        userContest.deleted = DEFAULT_DELETED;
        return userContest;
    }

    @BeforeEach
    public void initTest() {
        userContest = createEntity();
    }

    @Test
    public void createUserContest() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the UserContest
        userContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userContest)
            .when()
            .post("/api/user-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the UserContest in the database
        var userContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userContestList).hasSize(databaseSizeBeforeCreate + 1);
        var testUserContest = userContestList.stream().filter(it -> userContest.id.equals(it.id)).findFirst().get();
        assertThat(testUserContest.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testUserContest.userId).isEqualTo(DEFAULT_USER_ID);
        assertThat(testUserContest.contestId).isEqualTo(DEFAULT_CONTEST_ID);
        assertThat(testUserContest.rank).isEqualTo(DEFAULT_RANK);
        assertThat(testUserContest.score).isEqualTo(DEFAULT_SCORE);
        assertThat(testUserContest.lastUpdated).isEqualTo(DEFAULT_LAST_UPDATED);
        assertThat(testUserContest.created).isEqualTo(DEFAULT_CREATED);
        assertThat(testUserContest.completed).isEqualTo(DEFAULT_COMPLETED);
        assertThat(testUserContest.deleted).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    public void createUserContestWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the UserContest with an existing ID
        userContest.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userContest)
            .when()
            .post("/api/user-contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the UserContest in the database
        var userContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userContestList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateUserContest() {
        // Initialize the database
        userContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userContest)
            .when()
            .post("/api/user-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the userContest
        var updatedUserContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests/{id}", userContest.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the userContest
        updatedUserContest.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedUserContest.userId = UPDATED_USER_ID;
        updatedUserContest.contestId = UPDATED_CONTEST_ID;
        updatedUserContest.rank = UPDATED_RANK;
        updatedUserContest.score = UPDATED_SCORE;
        updatedUserContest.lastUpdated = UPDATED_LAST_UPDATED;
        updatedUserContest.created = UPDATED_CREATED;
        updatedUserContest.completed = UPDATED_COMPLETED;
        updatedUserContest.deleted = UPDATED_DELETED;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedUserContest)
            .when()
            .put("/api/user-contests")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the UserContest in the database
        var userContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userContestList).hasSize(databaseSizeBeforeUpdate);
        var testUserContest = userContestList.stream().filter(it -> updatedUserContest.id.equals(it.id)).findFirst().get();
        assertThat(testUserContest.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testUserContest.userId).isEqualTo(UPDATED_USER_ID);
        assertThat(testUserContest.contestId).isEqualTo(UPDATED_CONTEST_ID);
        assertThat(testUserContest.rank).isEqualTo(UPDATED_RANK);
        assertThat(testUserContest.score).isEqualTo(UPDATED_SCORE);
        assertThat(testUserContest.lastUpdated).isEqualTo(UPDATED_LAST_UPDATED);
        assertThat(testUserContest.created).isEqualTo(UPDATED_CREATED);
        assertThat(testUserContest.completed).isEqualTo(UPDATED_COMPLETED);
        assertThat(testUserContest.deleted).isEqualTo(UPDATED_DELETED);
    }

    @Test
    public void updateNonExistingUserContest() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests")
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
            .body(userContest)
            .when()
            .put("/api/user-contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the UserContest in the database
        var userContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userContestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteUserContest() {
        // Initialize the database
        userContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userContest)
            .when()
            .post("/api/user-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the userContest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/user-contests/{id}", userContest.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var userContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userContestList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllUserContests() {
        // Initialize the database
        userContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userContest)
            .when()
            .post("/api/user-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the userContestList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(userContest.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("userId", hasItem(DEFAULT_USER_ID.intValue()))            .body("contestId", hasItem(DEFAULT_CONTEST_ID.intValue()))            .body("rank", hasItem(DEFAULT_RANK.intValue()))            .body("score", hasItem(DEFAULT_SCORE.intValue()))            .body("lastUpdated", hasItem(TestUtil.formatDateTime(DEFAULT_LAST_UPDATED)))            .body("created", hasItem(TestUtil.formatDateTime(DEFAULT_CREATED)))            .body("completed", hasItem(DEFAULT_COMPLETED.booleanValue()))            .body("deleted", hasItem(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    public void getUserContest() {
        // Initialize the database
        userContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userContest)
            .when()
            .post("/api/user-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the userContest
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/user-contests/{id}", userContest.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the userContest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests/{id}", userContest.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(userContest.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("userId", is(DEFAULT_USER_ID.intValue()))
                .body("contestId", is(DEFAULT_CONTEST_ID.intValue()))
                .body("rank", is(DEFAULT_RANK.intValue()))
                .body("score", is(DEFAULT_SCORE.intValue()))
                .body("lastUpdated", is(TestUtil.formatDateTime(DEFAULT_LAST_UPDATED)))
                .body("created", is(TestUtil.formatDateTime(DEFAULT_CREATED)))
                .body("completed", is(DEFAULT_COMPLETED.booleanValue()))
                .body("deleted", is(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    public void getNonExistingUserContest() {
        // Get the userContest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-contests/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

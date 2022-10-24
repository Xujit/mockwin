package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.BannerContest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class BannerContestResourceTest {

    private static final TypeRef<BannerContest> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<BannerContest>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final String DEFAULT_APPROVED_FILE = "AAAAAAAAAA";
    private static final String UPDATED_APPROVED_FILE = "BBBBBBBBBB";

    private static final Long DEFAULT_CONTEST_ID = 1L;
    private static final Long UPDATED_CONTEST_ID = 2L;

    private static final Long DEFAULT_ASSIGNED_TO = 1L;
    private static final Long UPDATED_ASSIGNED_TO = 2L;

    private static final Long DEFAULT_USER_TYPE_ID = 1L;
    private static final Long UPDATED_USER_TYPE_ID = 2L;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_FULLNAME = "AAAAAAAAAA";
    private static final String UPDATED_FULLNAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";



    String adminToken;

    BannerContest bannerContest;

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
    public static BannerContest createEntity() {
        var bannerContest = new BannerContest();
        bannerContest.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        bannerContest.approvedFile = DEFAULT_APPROVED_FILE;
        bannerContest.contestId = DEFAULT_CONTEST_ID;
        bannerContest.assignedTo = DEFAULT_ASSIGNED_TO;
        bannerContest.userTypeId = DEFAULT_USER_TYPE_ID;
        bannerContest.status = DEFAULT_STATUS;
        bannerContest.fullname = DEFAULT_FULLNAME;
        bannerContest.comments = DEFAULT_COMMENTS;
        bannerContest.reason = DEFAULT_REASON;
        return bannerContest;
    }

    @BeforeEach
    public void initTest() {
        bannerContest = createEntity();
    }

    @Test
    public void createBannerContest() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the BannerContest
        bannerContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(bannerContest)
            .when()
            .post("/api/banner-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the BannerContest in the database
        var bannerContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(bannerContestList).hasSize(databaseSizeBeforeCreate + 1);
        var testBannerContest = bannerContestList.stream().filter(it -> bannerContest.id.equals(it.id)).findFirst().get();
        assertThat(testBannerContest.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testBannerContest.approvedFile).isEqualTo(DEFAULT_APPROVED_FILE);
        assertThat(testBannerContest.contestId).isEqualTo(DEFAULT_CONTEST_ID);
        assertThat(testBannerContest.assignedTo).isEqualTo(DEFAULT_ASSIGNED_TO);
        assertThat(testBannerContest.userTypeId).isEqualTo(DEFAULT_USER_TYPE_ID);
        assertThat(testBannerContest.status).isEqualTo(DEFAULT_STATUS);
        assertThat(testBannerContest.fullname).isEqualTo(DEFAULT_FULLNAME);
        assertThat(testBannerContest.comments).isEqualTo(DEFAULT_COMMENTS);
        assertThat(testBannerContest.reason).isEqualTo(DEFAULT_REASON);
    }

    @Test
    public void createBannerContestWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the BannerContest with an existing ID
        bannerContest.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(bannerContest)
            .when()
            .post("/api/banner-contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the BannerContest in the database
        var bannerContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(bannerContestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkApprovedFileIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        bannerContest.approvedFile = null;

        // Create the BannerContest, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(bannerContest)
            .when()
            .post("/api/banner-contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the BannerContest in the database
        var bannerContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(bannerContestList).hasSize(databaseSizeBeforeTest);
    }
    @Test
    public void checkAssignedToIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        bannerContest.assignedTo = null;

        // Create the BannerContest, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(bannerContest)
            .when()
            .post("/api/banner-contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the BannerContest in the database
        var bannerContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(bannerContestList).hasSize(databaseSizeBeforeTest);
    }
    @Test
    public void checkStatusIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        bannerContest.status = null;

        // Create the BannerContest, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(bannerContest)
            .when()
            .post("/api/banner-contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the BannerContest in the database
        var bannerContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(bannerContestList).hasSize(databaseSizeBeforeTest);
    }
    @Test
    public void checkFullnameIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        bannerContest.fullname = null;

        // Create the BannerContest, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(bannerContest)
            .when()
            .post("/api/banner-contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the BannerContest in the database
        var bannerContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(bannerContestList).hasSize(databaseSizeBeforeTest);
    }
    @Test
    public void checkCommentsIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        bannerContest.comments = null;

        // Create the BannerContest, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(bannerContest)
            .when()
            .post("/api/banner-contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the BannerContest in the database
        var bannerContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(bannerContestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateBannerContest() {
        // Initialize the database
        bannerContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(bannerContest)
            .when()
            .post("/api/banner-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the bannerContest
        var updatedBannerContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests/{id}", bannerContest.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the bannerContest
        updatedBannerContest.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedBannerContest.approvedFile = UPDATED_APPROVED_FILE;
        updatedBannerContest.contestId = UPDATED_CONTEST_ID;
        updatedBannerContest.assignedTo = UPDATED_ASSIGNED_TO;
        updatedBannerContest.userTypeId = UPDATED_USER_TYPE_ID;
        updatedBannerContest.status = UPDATED_STATUS;
        updatedBannerContest.fullname = UPDATED_FULLNAME;
        updatedBannerContest.comments = UPDATED_COMMENTS;
        updatedBannerContest.reason = UPDATED_REASON;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedBannerContest)
            .when()
            .put("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the BannerContest in the database
        var bannerContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(bannerContestList).hasSize(databaseSizeBeforeUpdate);
        var testBannerContest = bannerContestList.stream().filter(it -> updatedBannerContest.id.equals(it.id)).findFirst().get();
        assertThat(testBannerContest.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testBannerContest.approvedFile).isEqualTo(UPDATED_APPROVED_FILE);
        assertThat(testBannerContest.contestId).isEqualTo(UPDATED_CONTEST_ID);
        assertThat(testBannerContest.assignedTo).isEqualTo(UPDATED_ASSIGNED_TO);
        assertThat(testBannerContest.userTypeId).isEqualTo(UPDATED_USER_TYPE_ID);
        assertThat(testBannerContest.status).isEqualTo(UPDATED_STATUS);
        assertThat(testBannerContest.fullname).isEqualTo(UPDATED_FULLNAME);
        assertThat(testBannerContest.comments).isEqualTo(UPDATED_COMMENTS);
        assertThat(testBannerContest.reason).isEqualTo(UPDATED_REASON);
    }

    @Test
    public void updateNonExistingBannerContest() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
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
            .body(bannerContest)
            .when()
            .put("/api/banner-contests")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the BannerContest in the database
        var bannerContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(bannerContestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteBannerContest() {
        // Initialize the database
        bannerContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(bannerContest)
            .when()
            .post("/api/banner-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the bannerContest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/banner-contests/{id}", bannerContest.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var bannerContestList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(bannerContestList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllBannerContests() {
        // Initialize the database
        bannerContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(bannerContest)
            .when()
            .post("/api/banner-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the bannerContestList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(bannerContest.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("approvedFile", hasItem(DEFAULT_APPROVED_FILE))            .body("contestId", hasItem(DEFAULT_CONTEST_ID.intValue()))            .body("assignedTo", hasItem(DEFAULT_ASSIGNED_TO.intValue()))            .body("userTypeId", hasItem(DEFAULT_USER_TYPE_ID.intValue()))            .body("status", hasItem(DEFAULT_STATUS))            .body("fullname", hasItem(DEFAULT_FULLNAME))            .body("comments", hasItem(DEFAULT_COMMENTS))            .body("reason", hasItem(DEFAULT_REASON));
    }

    @Test
    public void getBannerContest() {
        // Initialize the database
        bannerContest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(bannerContest)
            .when()
            .post("/api/banner-contests")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the bannerContest
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/banner-contests/{id}", bannerContest.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the bannerContest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests/{id}", bannerContest.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(bannerContest.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("approvedFile", is(DEFAULT_APPROVED_FILE))
                .body("contestId", is(DEFAULT_CONTEST_ID.intValue()))
                .body("assignedTo", is(DEFAULT_ASSIGNED_TO.intValue()))
                .body("userTypeId", is(DEFAULT_USER_TYPE_ID.intValue()))
                .body("status", is(DEFAULT_STATUS))
                .body("fullname", is(DEFAULT_FULLNAME))
                .body("comments", is(DEFAULT_COMMENTS))
                .body("reason", is(DEFAULT_REASON));
    }

    @Test
    public void getNonExistingBannerContest() {
        // Get the bannerContest
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/banner-contests/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

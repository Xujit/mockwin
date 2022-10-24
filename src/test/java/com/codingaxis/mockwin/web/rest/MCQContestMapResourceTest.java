package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.MCQContestMap;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class MCQContestMapResourceTest {

    private static final TypeRef<MCQContestMap> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<MCQContestMap>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final Long DEFAULT_CONTEST_ID = 1L;
    private static final Long UPDATED_CONTEST_ID = 2L;

    private static final String DEFAULT_NOOFMCQS = "AAAAAAAAAA";
    private static final String UPDATED_NOOFMCQS = "BBBBBBBBBB";

    private static final String DEFAULT_SUB_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_SUB_CATEGORY = "BBBBBBBBBB";

    private static final Long DEFAULT_ASSIGNED_TO = 1L;
    private static final Long UPDATED_ASSIGNED_TO = 2L;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";



    String adminToken;

    MCQContestMap mCQContestMap;

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
    public static MCQContestMap createEntity() {
        var mCQContestMap = new MCQContestMap();
        mCQContestMap.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        mCQContestMap.contestId = DEFAULT_CONTEST_ID;
        mCQContestMap.noofmcqs = DEFAULT_NOOFMCQS;
        mCQContestMap.subCategory = DEFAULT_SUB_CATEGORY;
        mCQContestMap.assignedTo = DEFAULT_ASSIGNED_TO;
        mCQContestMap.status = DEFAULT_STATUS;
        return mCQContestMap;
    }

    @BeforeEach
    public void initTest() {
        mCQContestMap = createEntity();
    }

    @Test
    public void createMCQContestMap() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the MCQContestMap
        mCQContestMap = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContestMap)
            .when()
            .post("/api/mcq-contest-maps")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the MCQContestMap in the database
        var mCQContestMapList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mCQContestMapList).hasSize(databaseSizeBeforeCreate + 1);
        var testMCQContestMap = mCQContestMapList.stream().filter(it -> mCQContestMap.id.equals(it.id)).findFirst().get();
        assertThat(testMCQContestMap.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testMCQContestMap.contestId).isEqualTo(DEFAULT_CONTEST_ID);
        assertThat(testMCQContestMap.noofmcqs).isEqualTo(DEFAULT_NOOFMCQS);
        assertThat(testMCQContestMap.subCategory).isEqualTo(DEFAULT_SUB_CATEGORY);
        assertThat(testMCQContestMap.assignedTo).isEqualTo(DEFAULT_ASSIGNED_TO);
        assertThat(testMCQContestMap.status).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    public void createMCQContestMapWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the MCQContestMap with an existing ID
        mCQContestMap.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContestMap)
            .when()
            .post("/api/mcq-contest-maps")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the MCQContestMap in the database
        var mCQContestMapList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mCQContestMapList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateMCQContestMap() {
        // Initialize the database
        mCQContestMap = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContestMap)
            .when()
            .post("/api/mcq-contest-maps")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the mCQContestMap
        var updatedMCQContestMap = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps/{id}", mCQContestMap.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the mCQContestMap
        updatedMCQContestMap.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedMCQContestMap.contestId = UPDATED_CONTEST_ID;
        updatedMCQContestMap.noofmcqs = UPDATED_NOOFMCQS;
        updatedMCQContestMap.subCategory = UPDATED_SUB_CATEGORY;
        updatedMCQContestMap.assignedTo = UPDATED_ASSIGNED_TO;
        updatedMCQContestMap.status = UPDATED_STATUS;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedMCQContestMap)
            .when()
            .put("/api/mcq-contest-maps")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the MCQContestMap in the database
        var mCQContestMapList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mCQContestMapList).hasSize(databaseSizeBeforeUpdate);
        var testMCQContestMap = mCQContestMapList.stream().filter(it -> updatedMCQContestMap.id.equals(it.id)).findFirst().get();
        assertThat(testMCQContestMap.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testMCQContestMap.contestId).isEqualTo(UPDATED_CONTEST_ID);
        assertThat(testMCQContestMap.noofmcqs).isEqualTo(UPDATED_NOOFMCQS);
        assertThat(testMCQContestMap.subCategory).isEqualTo(UPDATED_SUB_CATEGORY);
        assertThat(testMCQContestMap.assignedTo).isEqualTo(UPDATED_ASSIGNED_TO);
        assertThat(testMCQContestMap.status).isEqualTo(UPDATED_STATUS);
    }

    @Test
    public void updateNonExistingMCQContestMap() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps")
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
            .body(mCQContestMap)
            .when()
            .put("/api/mcq-contest-maps")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the MCQContestMap in the database
        var mCQContestMapList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mCQContestMapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteMCQContestMap() {
        // Initialize the database
        mCQContestMap = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContestMap)
            .when()
            .post("/api/mcq-contest-maps")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the mCQContestMap
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/mcq-contest-maps/{id}", mCQContestMap.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var mCQContestMapList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mCQContestMapList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllMCQContestMaps() {
        // Initialize the database
        mCQContestMap = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContestMap)
            .when()
            .post("/api/mcq-contest-maps")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the mCQContestMapList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(mCQContestMap.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("contestId", hasItem(DEFAULT_CONTEST_ID.intValue()))            .body("noofmcqs", hasItem(DEFAULT_NOOFMCQS))            .body("subCategory", hasItem(DEFAULT_SUB_CATEGORY))            .body("assignedTo", hasItem(DEFAULT_ASSIGNED_TO.intValue()))            .body("status", hasItem(DEFAULT_STATUS));
    }

    @Test
    public void getMCQContestMap() {
        // Initialize the database
        mCQContestMap = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mCQContestMap)
            .when()
            .post("/api/mcq-contest-maps")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the mCQContestMap
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/mcq-contest-maps/{id}", mCQContestMap.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the mCQContestMap
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps/{id}", mCQContestMap.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(mCQContestMap.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("contestId", is(DEFAULT_CONTEST_ID.intValue()))
                .body("noofmcqs", is(DEFAULT_NOOFMCQS))
                .body("subCategory", is(DEFAULT_SUB_CATEGORY))
                .body("assignedTo", is(DEFAULT_ASSIGNED_TO.intValue()))
                .body("status", is(DEFAULT_STATUS));
    }

    @Test
    public void getNonExistingMCQContestMap() {
        // Get the mCQContestMap
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcq-contest-maps/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

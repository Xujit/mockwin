package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.Assign;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class AssignResourceTest {

    private static final TypeRef<Assign> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<Assign>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final String DEFAULT_FULLNAME = "AAAAAAAAAA";
    private static final String UPDATED_FULLNAME = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Integer DEFAULT_USER_TYPE_ID = 1;
    private static final Integer UPDATED_USER_TYPE_ID = 2;

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";



    String adminToken;

    Assign assign;

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
    public static Assign createEntity() {
        var assign = new Assign();
        assign.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        assign.fullname = DEFAULT_FULLNAME;
        assign.status = DEFAULT_STATUS;
        assign.userTypeId = DEFAULT_USER_TYPE_ID;
        assign.comments = DEFAULT_COMMENTS;
        return assign;
    }

    @BeforeEach
    public void initTest() {
        assign = createEntity();
    }

    @Test
    public void createAssign() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Assign
        assign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assign)
            .when()
            .post("/api/assigns")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the Assign in the database
        var assignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignList).hasSize(databaseSizeBeforeCreate + 1);
        var testAssign = assignList.stream().filter(it -> assign.id.equals(it.id)).findFirst().get();
        assertThat(testAssign.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testAssign.fullname).isEqualTo(DEFAULT_FULLNAME);
        assertThat(testAssign.status).isEqualTo(DEFAULT_STATUS);
        assertThat(testAssign.userTypeId).isEqualTo(DEFAULT_USER_TYPE_ID);
        assertThat(testAssign.comments).isEqualTo(DEFAULT_COMMENTS);
    }

    @Test
    public void createAssignWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Assign with an existing ID
        assign.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assign)
            .when()
            .post("/api/assigns")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Assign in the database
        var assignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkFullnameIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        assign.fullname = null;

        // Create the Assign, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assign)
            .when()
            .post("/api/assigns")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Assign in the database
        var assignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignList).hasSize(databaseSizeBeforeTest);
    }
    @Test
    public void checkStatusIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        assign.status = null;

        // Create the Assign, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assign)
            .when()
            .post("/api/assigns")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Assign in the database
        var assignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateAssign() {
        // Initialize the database
        assign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assign)
            .when()
            .post("/api/assigns")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the assign
        var updatedAssign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns/{id}", assign.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the assign
        updatedAssign.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedAssign.fullname = UPDATED_FULLNAME;
        updatedAssign.status = UPDATED_STATUS;
        updatedAssign.userTypeId = UPDATED_USER_TYPE_ID;
        updatedAssign.comments = UPDATED_COMMENTS;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedAssign)
            .when()
            .put("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the Assign in the database
        var assignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignList).hasSize(databaseSizeBeforeUpdate);
        var testAssign = assignList.stream().filter(it -> updatedAssign.id.equals(it.id)).findFirst().get();
        assertThat(testAssign.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testAssign.fullname).isEqualTo(UPDATED_FULLNAME);
        assertThat(testAssign.status).isEqualTo(UPDATED_STATUS);
        assertThat(testAssign.userTypeId).isEqualTo(UPDATED_USER_TYPE_ID);
        assertThat(testAssign.comments).isEqualTo(UPDATED_COMMENTS);
    }

    @Test
    public void updateNonExistingAssign() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
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
            .body(assign)
            .when()
            .put("/api/assigns")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Assign in the database
        var assignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteAssign() {
        // Initialize the database
        assign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assign)
            .when()
            .post("/api/assigns")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the assign
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/assigns/{id}", assign.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var assignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllAssigns() {
        // Initialize the database
        assign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assign)
            .when()
            .post("/api/assigns")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the assignList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(assign.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("fullname", hasItem(DEFAULT_FULLNAME))            .body("status", hasItem(DEFAULT_STATUS))            .body("userTypeId", hasItem(DEFAULT_USER_TYPE_ID.intValue()))            .body("comments", hasItem(DEFAULT_COMMENTS));
    }

    @Test
    public void getAssign() {
        // Initialize the database
        assign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assign)
            .when()
            .post("/api/assigns")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the assign
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/assigns/{id}", assign.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the assign
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns/{id}", assign.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(assign.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("fullname", is(DEFAULT_FULLNAME))
                .body("status", is(DEFAULT_STATUS))
                .body("userTypeId", is(DEFAULT_USER_TYPE_ID.intValue()))
                .body("comments", is(DEFAULT_COMMENTS));
    }

    @Test
    public void getNonExistingAssign() {
        // Get the assign
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assigns/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

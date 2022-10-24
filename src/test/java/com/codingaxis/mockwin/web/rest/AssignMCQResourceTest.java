package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.AssignMCQ;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class AssignMCQResourceTest {

    private static final TypeRef<AssignMCQ> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<AssignMCQ>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final Integer DEFAULT_MCQ_ID = 1;
    private static final Integer UPDATED_MCQ_ID = 2;

    private static final Boolean DEFAULT_APPROVED = false;
    private static final Boolean UPDATED_APPROVED = true;

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";



    String adminToken;

    AssignMCQ assignMCQ;

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
    public static AssignMCQ createEntity() {
        var assignMCQ = new AssignMCQ();
        assignMCQ.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        assignMCQ.mcqId = DEFAULT_MCQ_ID;
        assignMCQ.approved = DEFAULT_APPROVED;
        assignMCQ.reason = DEFAULT_REASON;
        return assignMCQ;
    }

    @BeforeEach
    public void initTest() {
        assignMCQ = createEntity();
    }

    @Test
    public void createAssignMCQ() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the AssignMCQ
        assignMCQ = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assignMCQ)
            .when()
            .post("/api/assign-mcqs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the AssignMCQ in the database
        var assignMCQList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignMCQList).hasSize(databaseSizeBeforeCreate + 1);
        var testAssignMCQ = assignMCQList.stream().filter(it -> assignMCQ.id.equals(it.id)).findFirst().get();
        assertThat(testAssignMCQ.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testAssignMCQ.mcqId).isEqualTo(DEFAULT_MCQ_ID);
        assertThat(testAssignMCQ.approved).isEqualTo(DEFAULT_APPROVED);
        assertThat(testAssignMCQ.reason).isEqualTo(DEFAULT_REASON);
    }

    @Test
    public void createAssignMCQWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the AssignMCQ with an existing ID
        assignMCQ.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assignMCQ)
            .when()
            .post("/api/assign-mcqs")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the AssignMCQ in the database
        var assignMCQList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignMCQList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateAssignMCQ() {
        // Initialize the database
        assignMCQ = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assignMCQ)
            .when()
            .post("/api/assign-mcqs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the assignMCQ
        var updatedAssignMCQ = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs/{id}", assignMCQ.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the assignMCQ
        updatedAssignMCQ.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedAssignMCQ.mcqId = UPDATED_MCQ_ID;
        updatedAssignMCQ.approved = UPDATED_APPROVED;
        updatedAssignMCQ.reason = UPDATED_REASON;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedAssignMCQ)
            .when()
            .put("/api/assign-mcqs")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the AssignMCQ in the database
        var assignMCQList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignMCQList).hasSize(databaseSizeBeforeUpdate);
        var testAssignMCQ = assignMCQList.stream().filter(it -> updatedAssignMCQ.id.equals(it.id)).findFirst().get();
        assertThat(testAssignMCQ.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testAssignMCQ.mcqId).isEqualTo(UPDATED_MCQ_ID);
        assertThat(testAssignMCQ.approved).isEqualTo(UPDATED_APPROVED);
        assertThat(testAssignMCQ.reason).isEqualTo(UPDATED_REASON);
    }

    @Test
    public void updateNonExistingAssignMCQ() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs")
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
            .body(assignMCQ)
            .when()
            .put("/api/assign-mcqs")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the AssignMCQ in the database
        var assignMCQList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignMCQList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteAssignMCQ() {
        // Initialize the database
        assignMCQ = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assignMCQ)
            .when()
            .post("/api/assign-mcqs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the assignMCQ
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/assign-mcqs/{id}", assignMCQ.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var assignMCQList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(assignMCQList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllAssignMCQS() {
        // Initialize the database
        assignMCQ = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assignMCQ)
            .when()
            .post("/api/assign-mcqs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the assignMCQList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(assignMCQ.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("mcqId", hasItem(DEFAULT_MCQ_ID.intValue()))            .body("approved", hasItem(DEFAULT_APPROVED.booleanValue()))            .body("reason", hasItem(DEFAULT_REASON));
    }

    @Test
    public void getAssignMCQ() {
        // Initialize the database
        assignMCQ = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(assignMCQ)
            .when()
            .post("/api/assign-mcqs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the assignMCQ
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/assign-mcqs/{id}", assignMCQ.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the assignMCQ
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs/{id}", assignMCQ.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(assignMCQ.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("mcqId", is(DEFAULT_MCQ_ID.intValue()))
                .body("approved", is(DEFAULT_APPROVED.booleanValue()))
                .body("reason", is(DEFAULT_REASON));
    }

    @Test
    public void getNonExistingAssignMCQ() {
        // Get the assignMCQ
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/assign-mcqs/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

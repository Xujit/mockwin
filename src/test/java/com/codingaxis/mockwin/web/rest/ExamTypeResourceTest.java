package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.ExamType;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class ExamTypeResourceTest {

    private static final TypeRef<ExamType> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<ExamType>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final String DEFAULT_EXAM_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_EXAM_TYPE = "BBBBBBBBBB";



    String adminToken;

    ExamType examType;

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
    public static ExamType createEntity() {
        var examType = new ExamType();
        examType.examType = DEFAULT_EXAM_TYPE;
        return examType;
    }

    @BeforeEach
    public void initTest() {
        examType = createEntity();
    }

    @Test
    public void createExamType() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ExamType
        examType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(examType)
            .when()
            .post("/api/exam-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the ExamType in the database
        var examTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(examTypeList).hasSize(databaseSizeBeforeCreate + 1);
        var testExamType = examTypeList.stream().filter(it -> examType.id.equals(it.id)).findFirst().get();
        assertThat(testExamType.examType).isEqualTo(DEFAULT_EXAM_TYPE);
    }

    @Test
    public void createExamTypeWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ExamType with an existing ID
        examType.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(examType)
            .when()
            .post("/api/exam-types")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ExamType in the database
        var examTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(examTypeList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateExamType() {
        // Initialize the database
        examType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(examType)
            .when()
            .post("/api/exam-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the examType
        var updatedExamType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types/{id}", examType.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the examType
        updatedExamType.examType = UPDATED_EXAM_TYPE;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedExamType)
            .when()
            .put("/api/exam-types")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the ExamType in the database
        var examTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(examTypeList).hasSize(databaseSizeBeforeUpdate);
        var testExamType = examTypeList.stream().filter(it -> updatedExamType.id.equals(it.id)).findFirst().get();
        assertThat(testExamType.examType).isEqualTo(UPDATED_EXAM_TYPE);
    }

    @Test
    public void updateNonExistingExamType() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types")
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
            .body(examType)
            .when()
            .put("/api/exam-types")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ExamType in the database
        var examTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(examTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteExamType() {
        // Initialize the database
        examType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(examType)
            .when()
            .post("/api/exam-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the examType
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/exam-types/{id}", examType.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var examTypeList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(examTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllExamTypes() {
        // Initialize the database
        examType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(examType)
            .when()
            .post("/api/exam-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the examTypeList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(examType.id.intValue()))
            .body("examType", hasItem(DEFAULT_EXAM_TYPE));
    }

    @Test
    public void getExamType() {
        // Initialize the database
        examType = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(examType)
            .when()
            .post("/api/exam-types")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the examType
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/exam-types/{id}", examType.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the examType
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types/{id}", examType.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(examType.id.intValue()))
            
                .body("examType", is(DEFAULT_EXAM_TYPE));
    }

    @Test
    public void getNonExistingExamType() {
        // Get the examType
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/exam-types/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

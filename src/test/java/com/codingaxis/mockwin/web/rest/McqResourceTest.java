package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.Mcq;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class McqResourceTest {

    private static final TypeRef<Mcq> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<Mcq>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final String DEFAULT_QUESTION = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION = "BBBBBBBBBB";

    private static final String DEFAULT_CHAPTER = "AAAAAAAAAA";
    private static final String UPDATED_CHAPTER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final Long DEFAULT_CREATED_BY = 1L;
    private static final Long UPDATED_CREATED_BY = 2L;



    String adminToken;

    Mcq mcq;

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
    public static Mcq createEntity() {
        var mcq = new Mcq();
        mcq.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        mcq.question = DEFAULT_QUESTION;
        mcq.chapter = DEFAULT_CHAPTER;
        mcq.deleted = DEFAULT_DELETED;
        mcq.createdBy = DEFAULT_CREATED_BY;
        return mcq;
    }

    @BeforeEach
    public void initTest() {
        mcq = createEntity();
    }

    @Test
    public void createMcq() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Mcq
        mcq = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mcq)
            .when()
            .post("/api/mcqs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the Mcq in the database
        var mcqList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mcqList).hasSize(databaseSizeBeforeCreate + 1);
        var testMcq = mcqList.stream().filter(it -> mcq.id.equals(it.id)).findFirst().get();
        assertThat(testMcq.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testMcq.question).isEqualTo(DEFAULT_QUESTION);
        assertThat(testMcq.chapter).isEqualTo(DEFAULT_CHAPTER);
        assertThat(testMcq.deleted).isEqualTo(DEFAULT_DELETED);
        assertThat(testMcq.createdBy).isEqualTo(DEFAULT_CREATED_BY);
    }

    @Test
    public void createMcqWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Mcq with an existing ID
        mcq.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mcq)
            .when()
            .post("/api/mcqs")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Mcq in the database
        var mcqList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mcqList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateMcq() {
        // Initialize the database
        mcq = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mcq)
            .when()
            .post("/api/mcqs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the mcq
        var updatedMcq = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs/{id}", mcq.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the mcq
        updatedMcq.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedMcq.question = UPDATED_QUESTION;
        updatedMcq.chapter = UPDATED_CHAPTER;
        updatedMcq.deleted = UPDATED_DELETED;
        updatedMcq.createdBy = UPDATED_CREATED_BY;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedMcq)
            .when()
            .put("/api/mcqs")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the Mcq in the database
        var mcqList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mcqList).hasSize(databaseSizeBeforeUpdate);
        var testMcq = mcqList.stream().filter(it -> updatedMcq.id.equals(it.id)).findFirst().get();
        assertThat(testMcq.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testMcq.question).isEqualTo(UPDATED_QUESTION);
        assertThat(testMcq.chapter).isEqualTo(UPDATED_CHAPTER);
        assertThat(testMcq.deleted).isEqualTo(UPDATED_DELETED);
        assertThat(testMcq.createdBy).isEqualTo(UPDATED_CREATED_BY);
    }

    @Test
    public void updateNonExistingMcq() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs")
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
            .body(mcq)
            .when()
            .put("/api/mcqs")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Mcq in the database
        var mcqList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mcqList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteMcq() {
        // Initialize the database
        mcq = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mcq)
            .when()
            .post("/api/mcqs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the mcq
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/mcqs/{id}", mcq.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var mcqList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(mcqList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllMcqs() {
        // Initialize the database
        mcq = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mcq)
            .when()
            .post("/api/mcqs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the mcqList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(mcq.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("question", hasItem(DEFAULT_QUESTION))            .body("chapter", hasItem(DEFAULT_CHAPTER))            .body("deleted", hasItem(DEFAULT_DELETED.booleanValue()))            .body("createdBy", hasItem(DEFAULT_CREATED_BY.intValue()));
    }

    @Test
    public void getMcq() {
        // Initialize the database
        mcq = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(mcq)
            .when()
            .post("/api/mcqs")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the mcq
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/mcqs/{id}", mcq.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the mcq
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs/{id}", mcq.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(mcq.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("question", is(DEFAULT_QUESTION))
                .body("chapter", is(DEFAULT_CHAPTER))
                .body("deleted", is(DEFAULT_DELETED.booleanValue()))
                .body("createdBy", is(DEFAULT_CREATED_BY.intValue()));
    }

    @Test
    public void getNonExistingMcq() {
        // Get the mcq
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/mcqs/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

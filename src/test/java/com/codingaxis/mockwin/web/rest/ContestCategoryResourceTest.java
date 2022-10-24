package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.ContestCategory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class ContestCategoryResourceTest {

    private static final TypeRef<ContestCategory> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<ContestCategory>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final Long DEFAULT_CONTEST_ID = 1L;
    private static final Long UPDATED_CONTEST_ID = 2L;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";



    String adminToken;

    ContestCategory contestCategory;

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
    public static ContestCategory createEntity() {
        var contestCategory = new ContestCategory();
        contestCategory.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        contestCategory.contestId = DEFAULT_CONTEST_ID;
        contestCategory.status = DEFAULT_STATUS;
        return contestCategory;
    }

    @BeforeEach
    public void initTest() {
        contestCategory = createEntity();
    }

    @Test
    public void createContestCategory() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ContestCategory
        contestCategory = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestCategory)
            .when()
            .post("/api/contest-categories")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the ContestCategory in the database
        var contestCategoryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        var testContestCategory = contestCategoryList.stream().filter(it -> contestCategory.id.equals(it.id)).findFirst().get();
        assertThat(testContestCategory.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testContestCategory.contestId).isEqualTo(DEFAULT_CONTEST_ID);
        assertThat(testContestCategory.status).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    public void createContestCategoryWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ContestCategory with an existing ID
        contestCategory.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestCategory)
            .when()
            .post("/api/contest-categories")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ContestCategory in the database
        var contestCategoryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestCategoryList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateContestCategory() {
        // Initialize the database
        contestCategory = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestCategory)
            .when()
            .post("/api/contest-categories")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the contestCategory
        var updatedContestCategory = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories/{id}", contestCategory.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the contestCategory
        updatedContestCategory.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedContestCategory.contestId = UPDATED_CONTEST_ID;
        updatedContestCategory.status = UPDATED_STATUS;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedContestCategory)
            .when()
            .put("/api/contest-categories")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the ContestCategory in the database
        var contestCategoryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestCategoryList).hasSize(databaseSizeBeforeUpdate);
        var testContestCategory = contestCategoryList.stream().filter(it -> updatedContestCategory.id.equals(it.id)).findFirst().get();
        assertThat(testContestCategory.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testContestCategory.contestId).isEqualTo(UPDATED_CONTEST_ID);
        assertThat(testContestCategory.status).isEqualTo(UPDATED_STATUS);
    }

    @Test
    public void updateNonExistingContestCategory() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories")
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
            .body(contestCategory)
            .when()
            .put("/api/contest-categories")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ContestCategory in the database
        var contestCategoryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteContestCategory() {
        // Initialize the database
        contestCategory = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestCategory)
            .when()
            .post("/api/contest-categories")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the contestCategory
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/contest-categories/{id}", contestCategory.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var contestCategoryList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllContestCategories() {
        // Initialize the database
        contestCategory = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestCategory)
            .when()
            .post("/api/contest-categories")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the contestCategoryList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(contestCategory.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("contestId", hasItem(DEFAULT_CONTEST_ID.intValue()))            .body("status", hasItem(DEFAULT_STATUS));
    }

    @Test
    public void getContestCategory() {
        // Initialize the database
        contestCategory = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestCategory)
            .when()
            .post("/api/contest-categories")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the contestCategory
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/contest-categories/{id}", contestCategory.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the contestCategory
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories/{id}", contestCategory.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(contestCategory.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("contestId", is(DEFAULT_CONTEST_ID.intValue()))
                .body("status", is(DEFAULT_STATUS));
    }

    @Test
    public void getNonExistingContestCategory() {
        // Get the contestCategory
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-categories/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

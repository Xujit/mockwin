package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.ContestAssign;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class ContestAssignResourceTest {

    private static final TypeRef<ContestAssign> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<ContestAssign>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Long DEFAULT_DESIGNER_ID = 1L;
    private static final Long UPDATED_DESIGNER_ID = 2L;

    private static final Long DEFAULT_SUPER_SME_ID = 1L;
    private static final Long UPDATED_SUPER_SME_ID = 2L;

    private static final Long DEFAULT_SURVEYER_ID = 1L;
    private static final Long UPDATED_SURVEYER_ID = 2L;

    private static final Long DEFAULT_SME_ID = 1L;
    private static final Long UPDATED_SME_ID = 2L;



    String adminToken;

    ContestAssign contestAssign;

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
    public static ContestAssign createEntity() {
        var contestAssign = new ContestAssign();
        contestAssign.designerId = DEFAULT_DESIGNER_ID;
        contestAssign.superSmeId = DEFAULT_SUPER_SME_ID;
        contestAssign.surveyerId = DEFAULT_SURVEYER_ID;
        contestAssign.smeId = DEFAULT_SME_ID;
        return contestAssign;
    }

    @BeforeEach
    public void initTest() {
        contestAssign = createEntity();
    }

    @Test
    public void createContestAssign() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ContestAssign
        contestAssign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestAssign)
            .when()
            .post("/api/contest-assigns")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the ContestAssign in the database
        var contestAssignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestAssignList).hasSize(databaseSizeBeforeCreate + 1);
        var testContestAssign = contestAssignList.stream().filter(it -> contestAssign.id.equals(it.id)).findFirst().get();
        assertThat(testContestAssign.designerId).isEqualTo(DEFAULT_DESIGNER_ID);
        assertThat(testContestAssign.superSmeId).isEqualTo(DEFAULT_SUPER_SME_ID);
        assertThat(testContestAssign.surveyerId).isEqualTo(DEFAULT_SURVEYER_ID);
        assertThat(testContestAssign.smeId).isEqualTo(DEFAULT_SME_ID);
    }

    @Test
    public void createContestAssignWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the ContestAssign with an existing ID
        contestAssign.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestAssign)
            .when()
            .post("/api/contest-assigns")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ContestAssign in the database
        var contestAssignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestAssignList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateContestAssign() {
        // Initialize the database
        contestAssign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestAssign)
            .when()
            .post("/api/contest-assigns")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the contestAssign
        var updatedContestAssign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns/{id}", contestAssign.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the contestAssign
        updatedContestAssign.designerId = UPDATED_DESIGNER_ID;
        updatedContestAssign.superSmeId = UPDATED_SUPER_SME_ID;
        updatedContestAssign.surveyerId = UPDATED_SURVEYER_ID;
        updatedContestAssign.smeId = UPDATED_SME_ID;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedContestAssign)
            .when()
            .put("/api/contest-assigns")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the ContestAssign in the database
        var contestAssignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestAssignList).hasSize(databaseSizeBeforeUpdate);
        var testContestAssign = contestAssignList.stream().filter(it -> updatedContestAssign.id.equals(it.id)).findFirst().get();
        assertThat(testContestAssign.designerId).isEqualTo(UPDATED_DESIGNER_ID);
        assertThat(testContestAssign.superSmeId).isEqualTo(UPDATED_SUPER_SME_ID);
        assertThat(testContestAssign.surveyerId).isEqualTo(UPDATED_SURVEYER_ID);
        assertThat(testContestAssign.smeId).isEqualTo(UPDATED_SME_ID);
    }

    @Test
    public void updateNonExistingContestAssign() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns")
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
            .body(contestAssign)
            .when()
            .put("/api/contest-assigns")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the ContestAssign in the database
        var contestAssignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestAssignList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteContestAssign() {
        // Initialize the database
        contestAssign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestAssign)
            .when()
            .post("/api/contest-assigns")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the contestAssign
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/contest-assigns/{id}", contestAssign.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var contestAssignList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(contestAssignList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllContestAssigns() {
        // Initialize the database
        contestAssign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestAssign)
            .when()
            .post("/api/contest-assigns")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the contestAssignList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(contestAssign.id.intValue()))
            .body("designerId", hasItem(DEFAULT_DESIGNER_ID.intValue()))            .body("superSmeId", hasItem(DEFAULT_SUPER_SME_ID.intValue()))            .body("surveyerId", hasItem(DEFAULT_SURVEYER_ID.intValue()))            .body("smeId", hasItem(DEFAULT_SME_ID.intValue()));
    }

    @Test
    public void getContestAssign() {
        // Initialize the database
        contestAssign = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(contestAssign)
            .when()
            .post("/api/contest-assigns")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the contestAssign
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/contest-assigns/{id}", contestAssign.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the contestAssign
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns/{id}", contestAssign.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(contestAssign.id.intValue()))
            
                .body("designerId", is(DEFAULT_DESIGNER_ID.intValue()))
                .body("superSmeId", is(DEFAULT_SUPER_SME_ID.intValue()))
                .body("surveyerId", is(DEFAULT_SURVEYER_ID.intValue()))
                .body("smeId", is(DEFAULT_SME_ID.intValue()));
    }

    @Test
    public void getNonExistingContestAssign() {
        // Get the contestAssign
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/contest-assigns/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

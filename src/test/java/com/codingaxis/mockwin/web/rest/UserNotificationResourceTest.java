package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.UserNotification;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseFactory;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import java.util.List;
    
@QuarkusTest
public class UserNotificationResourceTest {

    private static final TypeRef<UserNotification> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<UserNotification>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_READ = false;
    private static final Boolean UPDATED_READ = true;



    String adminToken;

    UserNotification userNotification;

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
    public static UserNotification createEntity() {
        var userNotification = new UserNotification();
        userNotification.userId = DEFAULT_USER_ID;
        userNotification.message = DEFAULT_MESSAGE;
        userNotification.read = DEFAULT_READ;
        return userNotification;
    }

    @BeforeEach
    public void initTest() {
        userNotification = createEntity();
    }

    @Test
    public void createUserNotification() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the UserNotification
        userNotification = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userNotification)
            .when()
            .post("/api/user-notifications")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the UserNotification in the database
        var userNotificationList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userNotificationList).hasSize(databaseSizeBeforeCreate + 1);
        var testUserNotification = userNotificationList.stream().filter(it -> userNotification.id.equals(it.id)).findFirst().get();
        assertThat(testUserNotification.userId).isEqualTo(DEFAULT_USER_ID);
        assertThat(testUserNotification.message).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testUserNotification.read).isEqualTo(DEFAULT_READ);
    }

    @Test
    public void createUserNotificationWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the UserNotification with an existing ID
        userNotification.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userNotification)
            .when()
            .post("/api/user-notifications")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the UserNotification in the database
        var userNotificationList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userNotificationList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateUserNotification() {
        // Initialize the database
        userNotification = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userNotification)
            .when()
            .post("/api/user-notifications")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the userNotification
        var updatedUserNotification = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications/{id}", userNotification.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the userNotification
        updatedUserNotification.userId = UPDATED_USER_ID;
        updatedUserNotification.message = UPDATED_MESSAGE;
        updatedUserNotification.read = UPDATED_READ;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedUserNotification)
            .when()
            .put("/api/user-notifications")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the UserNotification in the database
        var userNotificationList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userNotificationList).hasSize(databaseSizeBeforeUpdate);
        var testUserNotification = userNotificationList.stream().filter(it -> updatedUserNotification.id.equals(it.id)).findFirst().get();
        assertThat(testUserNotification.userId).isEqualTo(UPDATED_USER_ID);
        assertThat(testUserNotification.message).isEqualTo(UPDATED_MESSAGE);
        assertThat(testUserNotification.read).isEqualTo(UPDATED_READ);
    }

    @Test
    public void updateNonExistingUserNotification() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications")
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
            .body(userNotification)
            .when()
            .put("/api/user-notifications")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the UserNotification in the database
        var userNotificationList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userNotificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteUserNotification() {
        // Initialize the database
        userNotification = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userNotification)
            .when()
            .post("/api/user-notifications")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the userNotification
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/user-notifications/{id}", userNotification.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var userNotificationList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(userNotificationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllUserNotifications() {
        // Initialize the database
        userNotification = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userNotification)
            .when()
            .post("/api/user-notifications")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the userNotificationList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(userNotification.id.intValue()))
            .body("userId", hasItem(DEFAULT_USER_ID.intValue()))            .body("message", hasItem(DEFAULT_MESSAGE))            .body("read", hasItem(DEFAULT_READ.booleanValue()));
    }

    @Test
    public void getUserNotification() {
        // Initialize the database
        userNotification = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(userNotification)
            .when()
            .post("/api/user-notifications")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the userNotification
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/user-notifications/{id}", userNotification.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the userNotification
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications/{id}", userNotification.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(userNotification.id.intValue()))
            
                .body("userId", is(DEFAULT_USER_ID.intValue()))
                .body("message", is(DEFAULT_MESSAGE))
                .body("read", is(DEFAULT_READ.booleanValue()));
    }

    @Test
    public void getNonExistingUserNotification() {
        // Get the userNotification
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/user-notifications/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

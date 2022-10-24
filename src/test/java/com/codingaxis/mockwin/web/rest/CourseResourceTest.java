package com.codingaxis.mockwin.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.codingaxis.mockwin.TestUtil;
import com.codingaxis.mockwin.domain.Course;
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
public class CourseResourceTest {

    private static final TypeRef<Course> ENTITY_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<Course>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {
    };

    private static final Integer DEFAULT_MODIFICATION_COUNTER = 1;
    private static final Integer UPDATED_MODIFICATION_COUNTER = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_LAST_UPDATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_UPDATED = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Long DEFAULT_COMPET_EXAM_ID = 1L;
    private static final Long UPDATED_COMPET_EXAM_ID = 2L;

    private static final String DEFAULT_CURRICULUM = "AAAAAAAAAA";
    private static final String UPDATED_CURRICULUM = "BBBBBBBBBB";

    private static final String DEFAULT_PRIZE_MECH = "AAAAAAAAAA";
    private static final String UPDATED_PRIZE_MECH = "BBBBBBBBBB";



    String adminToken;

    Course course;

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
    public static Course createEntity() {
        var course = new Course();
        course.modificationCounter = DEFAULT_MODIFICATION_COUNTER;
        course.name = DEFAULT_NAME;
        course.created = DEFAULT_CREATED;
        course.lastUpdated = DEFAULT_LAST_UPDATED;
        course.status = DEFAULT_STATUS;
        course.competExamId = DEFAULT_COMPET_EXAM_ID;
        course.curriculum = DEFAULT_CURRICULUM;
        course.prizeMech = DEFAULT_PRIZE_MECH;
        return course;
    }

    @BeforeEach
    public void initTest() {
        course = createEntity();
    }

    @Test
    public void createCourse() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Course
        course = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(course)
            .when()
            .post("/api/courses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Validate the Course in the database
        var courseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(courseList).hasSize(databaseSizeBeforeCreate + 1);
        var testCourse = courseList.stream().filter(it -> course.id.equals(it.id)).findFirst().get();
        assertThat(testCourse.modificationCounter).isEqualTo(DEFAULT_MODIFICATION_COUNTER);
        assertThat(testCourse.name).isEqualTo(DEFAULT_NAME);
        assertThat(testCourse.created).isEqualTo(DEFAULT_CREATED);
        assertThat(testCourse.lastUpdated).isEqualTo(DEFAULT_LAST_UPDATED);
        assertThat(testCourse.status).isEqualTo(DEFAULT_STATUS);
        assertThat(testCourse.competExamId).isEqualTo(DEFAULT_COMPET_EXAM_ID);
        assertThat(testCourse.curriculum).isEqualTo(DEFAULT_CURRICULUM);
        assertThat(testCourse.prizeMech).isEqualTo(DEFAULT_PRIZE_MECH);
    }

    @Test
    public void createCourseWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Course with an existing ID
        course.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(course)
            .when()
            .post("/api/courses")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Course in the database
        var courseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(courseList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void updateCourse() {
        // Initialize the database
        course = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(course)
            .when()
            .post("/api/courses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the course
        var updatedCourse = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses/{id}", course.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().body().as(ENTITY_TYPE);

        // Update the course
        updatedCourse.modificationCounter = UPDATED_MODIFICATION_COUNTER;
        updatedCourse.name = UPDATED_NAME;
        updatedCourse.created = UPDATED_CREATED;
        updatedCourse.lastUpdated = UPDATED_LAST_UPDATED;
        updatedCourse.status = UPDATED_STATUS;
        updatedCourse.competExamId = UPDATED_COMPET_EXAM_ID;
        updatedCourse.curriculum = UPDATED_CURRICULUM;
        updatedCourse.prizeMech = UPDATED_PRIZE_MECH;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedCourse)
            .when()
            .put("/api/courses")
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the Course in the database
        var courseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        var testCourse = courseList.stream().filter(it -> updatedCourse.id.equals(it.id)).findFirst().get();
        assertThat(testCourse.modificationCounter).isEqualTo(UPDATED_MODIFICATION_COUNTER);
        assertThat(testCourse.name).isEqualTo(UPDATED_NAME);
        assertThat(testCourse.created).isEqualTo(UPDATED_CREATED);
        assertThat(testCourse.lastUpdated).isEqualTo(UPDATED_LAST_UPDATED);
        assertThat(testCourse.status).isEqualTo(UPDATED_STATUS);
        assertThat(testCourse.competExamId).isEqualTo(UPDATED_COMPET_EXAM_ID);
        assertThat(testCourse.curriculum).isEqualTo(UPDATED_CURRICULUM);
        assertThat(testCourse.prizeMech).isEqualTo(UPDATED_PRIZE_MECH);
    }

    @Test
    public void updateNonExistingCourse() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses")
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
            .body(course)
            .when()
            .put("/api/courses")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Course in the database
        var courseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCourse() {
        // Initialize the database
        course = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(course)
            .when()
            .post("/api/courses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the course
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/courses/{id}", course.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var courseList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract().as(LIST_OF_ENTITY_TYPE);

        assertThat(courseList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllCourses() {
        // Initialize the database
        course = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(course)
            .when()
            .post("/api/courses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        // Get all the courseList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(course.id.intValue()))
            .body("modificationCounter", hasItem(DEFAULT_MODIFICATION_COUNTER.intValue()))            .body("name", hasItem(DEFAULT_NAME))            .body("created", hasItem(TestUtil.formatDateTime(DEFAULT_CREATED)))            .body("lastUpdated", hasItem(TestUtil.formatDateTime(DEFAULT_LAST_UPDATED)))            .body("status", hasItem(DEFAULT_STATUS))            .body("competExamId", hasItem(DEFAULT_COMPET_EXAM_ID.intValue()))            .body("curriculum", hasItem(DEFAULT_CURRICULUM))            .body("prizeMech", hasItem(DEFAULT_PRIZE_MECH));
    }

    @Test
    public void getCourse() {
        // Initialize the database
        course = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(course)
            .when()
            .post("/api/courses")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(ENTITY_TYPE);

        var response = // Get the course
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/courses/{id}", course.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ENTITY_TYPE);

        // Get the course
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses/{id}", course.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(course.id.intValue()))
            
                .body("modificationCounter", is(DEFAULT_MODIFICATION_COUNTER.intValue()))
                .body("name", is(DEFAULT_NAME))
                .body("created", is(TestUtil.formatDateTime(DEFAULT_CREATED)))
                .body("lastUpdated", is(TestUtil.formatDateTime(DEFAULT_LAST_UPDATED)))
                .body("status", is(DEFAULT_STATUS))
                .body("competExamId", is(DEFAULT_COMPET_EXAM_ID.intValue()))
                .body("curriculum", is(DEFAULT_CURRICULUM))
                .body("prizeMech", is(DEFAULT_PRIZE_MECH));
    }

    @Test
    public void getNonExistingCourse() {
        // Get the course
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/courses/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}

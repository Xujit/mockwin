package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.Course;
import com.codingaxis.mockwin.web.rest.errors.BadRequestAlertException;
import com.codingaxis.mockwin.web.util.HeaderUtil;
import com.codingaxis.mockwin.web.util.ResponseUtil;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.codingaxis.mockwin.domain.Course}.
 */
@Path("/api/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CourseResource {

    private final Logger log = LoggerFactory.getLogger(CourseResource.class);

    private static final String ENTITY_NAME = "mockwinfinalCourse";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /courses} : Create a new course.
     *
     * @param course the course to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new course, or with status {@code 400 (Bad Request)} if the course has already an ID.
     */
    @POST
    @Transactional
    public Response createCourse(Course course, @Context UriInfo uriInfo) {
        log.debug("REST request to save Course : {}", course);
        if (course.id != null) {
            throw new BadRequestAlertException("A new course cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = Course.persistOrUpdate(course);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /courses} : Updates an existing course.
     *
     * @param course the course to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated course,
     * or with status {@code 400 (Bad Request)} if the course is not valid,
     * or with status {@code 500 (Internal Server Error)} if the course couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateCourse(Course course) {
        log.debug("REST request to update Course : {}", course);
        if (course.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = Course.persistOrUpdate(course);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, course.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /courses/:id} : delete the "id" course.
     *
     * @param id the id of the course to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteCourse(@PathParam("id") Long id) {
        log.debug("REST request to delete Course : {}", id);
        Course.findByIdOptional(id).ifPresent(course -> {
            course.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /courses} : get all the courses.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of courses in body.
     */
    @GET
    public List<Course> getAllCourses() {
        log.debug("REST request to get all Courses");
        return Course.findAll().list();
    }


    /**
     * {@code GET  /courses/:id} : get the "id" course.
     *
     * @param id the id of the course to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the course, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getCourse(@PathParam("id") Long id) {
        log.debug("REST request to get Course : {}", id);
        Optional<Course> course = Course.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(course);
    }
}

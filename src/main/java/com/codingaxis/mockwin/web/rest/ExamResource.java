package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codingaxis.mockwin.domain.Exam;
import com.codingaxis.mockwin.web.rest.errors.BadRequestAlertException;
import com.codingaxis.mockwin.web.util.HeaderUtil;
import com.codingaxis.mockwin.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.codingaxis.mockwin.domain.Exam}.
 */
@Path("/api/exams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ExamResource {

  private final Logger log = LoggerFactory.getLogger(ExamResource.class);

  private static final String ENTITY_NAME = "mockwinfinalExam";

  @ConfigProperty(name = "application.name")
  String applicationName;

  /**
   * {@code POST  /exams} : Create a new exam.
   *
   * @param exam the exam to create.
   * @return the {@link Response} with status {@code 201 (Created)} and with body the new exam, or with status
   *         {@code 400 (Bad Request)} if the exam has already an ID.
   */
  @POST
  @Transactional
  public Response createExam(@Valid Exam exam, @Context UriInfo uriInfo) {

    this.log.debug("REST request to save Exam : {}", exam);
    if (exam.id != null) {
      throw new BadRequestAlertException("A new exam cannot already have an ID", ENTITY_NAME, "idexists");
    }
    var result = Exam.persistOrUpdate(exam);
    var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
    HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code PUT  /exams} : Updates an existing exam.
   *
   * @param exam the exam to update.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the updated exam, or with status
   *         {@code 400 (Bad Request)} if the exam is not valid, or with status {@code 500 (Internal Server Error)} if
   *         the exam couldn't be updated.
   */
  @PUT
  @Transactional
  public Response updateExam(@Valid Exam exam) {

    this.log.debug("REST request to update Exam : {}", exam);
    if (exam.id == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    var result = Exam.persistOrUpdate(exam);
    var response = Response.ok().entity(result);
    HeaderUtil.createEntityUpdateAlert(this.applicationName, true, ENTITY_NAME, exam.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code DELETE  /exams/:id} : delete the "id" exam.
   *
   * @param id the id of the exam to delete.
   * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteExam(@PathParam("id") Long id) {

    this.log.debug("REST request to delete Exam : {}", id);
    Exam.findByIdOptional(id).ifPresent(exam -> {
      exam.delete();
    });
    var response = Response.noContent();
    HeaderUtil.createEntityDeletionAlert(this.applicationName, true, ENTITY_NAME, id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code GET  /exams} : get all the exams. * @return the {@link Response} with status {@code 200 (OK)} and the list
   * of exams in body.
   */
  @GET
  public List<Exam> getAllExams() {

    this.log.debug("REST request to get all Exams");
    return Exam.findAll().list();
  }

  /**
   * {@code GET  /exams/:id} : get the "id" exam.
   *
   * @param id the id of the exam to retrieve.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the exam, or with status
   *         {@code 404 (Not Found)}.
   */
  @GET
  @Path("/{id}")

  public Response getExam(@PathParam("id") Long id) {

    this.log.debug("REST request to get Exam : {}", id);
    Optional<Exam> exam = Exam.findByIdOptional(id);
    return ResponseUtil.wrapOrNotFound(exam);
  }
}

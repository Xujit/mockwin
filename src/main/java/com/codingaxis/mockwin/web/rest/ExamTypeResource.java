package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
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

import com.codingaxis.mockwin.domain.ExamType;
import com.codingaxis.mockwin.web.rest.errors.BadRequestAlertException;
import com.codingaxis.mockwin.web.util.HeaderUtil;
import com.codingaxis.mockwin.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.codingaxis.mockwin.domain.ExamType}.
 */
@Path("/api/exam-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ExamTypeResource {

  private final Logger log = LoggerFactory.getLogger(ExamTypeResource.class);

  private static final String ENTITY_NAME = "mockwinfinalExamType";

  @ConfigProperty(name = "application.name")
  String applicationName;

  /**
   * {@code POST  /exam-types} : Create a new examType.
   *
   * @param examType the examType to create.
   * @return the {@link Response} with status {@code 201 (Created)} and with body the new examType, or with status
   *         {@code 400 (Bad Request)} if the examType has already an ID.
   */
  @POST
  @Transactional
  public Response createExamType(ExamType examType, @Context UriInfo uriInfo) {

    this.log.debug("REST request to save ExamType : {}", examType);
    if (examType.id != null) {
      throw new BadRequestAlertException("A new examType cannot already have an ID", ENTITY_NAME, "idexists");
    }
    var result = ExamType.persistOrUpdate(examType);
    var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
    HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code PUT  /exam-types} : Updates an existing examType.
   *
   * @param examType the examType to update.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the updated examType, or with status
   *         {@code 400 (Bad Request)} if the examType is not valid, or with status {@code 500 (Internal Server Error)}
   *         if the examType couldn't be updated.
   */
  @PUT
  @Transactional
  public Response updateExamType(ExamType examType) {

    this.log.debug("REST request to update ExamType : {}", examType);
    if (examType.id == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    var result = ExamType.persistOrUpdate(examType);
    var response = Response.ok().entity(result);
    HeaderUtil.createEntityUpdateAlert(this.applicationName, true, ENTITY_NAME, examType.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code DELETE  /exam-types/:id} : delete the "id" examType.
   *
   * @param id the id of the examType to delete.
   * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteExamType(@PathParam("id") Long id) {

    this.log.debug("REST request to delete ExamType : {}", id);
    ExamType.findByIdOptional(id).ifPresent(examType -> {
      examType.delete();
    });
    var response = Response.noContent();
    HeaderUtil.createEntityDeletionAlert(this.applicationName, true, ENTITY_NAME, id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code GET  /exam-types} : get all the examTypes. * @return the {@link Response} with status {@code 200 (OK)} and
   * the list of examTypes in body.
   */
  @GET
  public List<ExamType> getAllExamTypes() {

    this.log.debug("REST request to get all ExamTypes");
    return ExamType.findAll().list();
  }

  /**
   * {@code GET  /exam-types/:id} : get the "id" examType.
   *
   * @param id the id of the examType to retrieve.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the examType, or with status
   *         {@code 404 (Not Found)}.
   */
  @GET
  @Path("/{id}")

  public Response getExamType(@PathParam("id") Long id) {

    this.log.debug("REST request to get ExamType : {}", id);
    Optional<ExamType> examType = ExamType.findByIdOptional(id);
    return ResponseUtil.wrapOrNotFound(examType);
  }
}

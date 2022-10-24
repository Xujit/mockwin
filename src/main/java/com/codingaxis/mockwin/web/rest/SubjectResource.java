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

import com.codingaxis.mockwin.domain.Subject;
import com.codingaxis.mockwin.web.rest.errors.BadRequestAlertException;
import com.codingaxis.mockwin.web.util.HeaderUtil;
import com.codingaxis.mockwin.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.codingaxis.mockwin.domain.Subject}.
 */
@Path("/api/subjects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class SubjectResource {

  private final Logger log = LoggerFactory.getLogger(SubjectResource.class);

  private static final String ENTITY_NAME = "mockwinfinalSubject";

  @ConfigProperty(name = "application.name")
  String applicationName;

  /**
   * {@code POST  /subjects} : Create a new subject.
   *
   * @param subject the subject to create.
   * @return the {@link Response} with status {@code 201 (Created)} and with body the new subject, or with status
   *         {@code 400 (Bad Request)} if the subject has already an ID.
   */
  @POST
  @Transactional
  public Response createSubject(Subject subject, @Context UriInfo uriInfo) {

    this.log.debug("REST request to save Subject : {}", subject);
    if (subject.id != null) {
      throw new BadRequestAlertException("A new subject cannot already have an ID", ENTITY_NAME, "idexists");
    }
    var result = Subject.persistOrUpdate(subject);
    var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
    HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code PUT  /subjects} : Updates an existing subject.
   *
   * @param subject the subject to update.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the updated subject, or with status
   *         {@code 400 (Bad Request)} if the subject is not valid, or with status {@code 500 (Internal Server Error)}
   *         if the subject couldn't be updated.
   */
  @PUT
  @Transactional
  public Response updateSubject(Subject subject) {

    this.log.debug("REST request to update Subject : {}", subject);
    if (subject.id == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    var result = Subject.persistOrUpdate(subject);
    var response = Response.ok().entity(result);
    HeaderUtil.createEntityUpdateAlert(this.applicationName, true, ENTITY_NAME, subject.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code DELETE  /subjects/:id} : delete the "id" subject.
   *
   * @param id the id of the subject to delete.
   * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteSubject(@PathParam("id") Long id) {

    this.log.debug("REST request to delete Subject : {}", id);
    Subject.findByIdOptional(id).ifPresent(subject -> {
      subject.delete();
    });
    var response = Response.noContent();
    HeaderUtil.createEntityDeletionAlert(this.applicationName, true, ENTITY_NAME, id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code GET  /subjects} : get all the subjects. * @return the {@link Response} with status {@code 200 (OK)} and the
   * list of subjects in body.
   */
  @GET
  public List<Subject> getAllSubjects() {

    this.log.debug("REST request to get all Subjects");
    return Subject.findAll().list();
  }

  /**
   * {@code GET  /subjects/:id} : get the "id" subject.
   *
   * @param id the id of the subject to retrieve.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the subject, or with status
   *         {@code 404 (Not Found)}.
   */
  @GET
  @Path("/{id}")

  public Response getSubject(@PathParam("id") Long id) {

    this.log.debug("REST request to get Subject : {}", id);
    Optional<Subject> subject = Subject.findByIdOptional(id);
    return ResponseUtil.wrapOrNotFound(subject);
  }
}

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

import com.codingaxis.mockwin.domain.Mcq;
import com.codingaxis.mockwin.web.rest.errors.BadRequestAlertException;
import com.codingaxis.mockwin.web.util.HeaderUtil;
import com.codingaxis.mockwin.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.codingaxis.mockwin.domain.Mcq}.
 */
@Path("/api/mcqs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class McqResource {

  private final Logger log = LoggerFactory.getLogger(McqResource.class);

  private static final String ENTITY_NAME = "mockwinfinalMcq";

  @ConfigProperty(name = "application.name")
  String applicationName;

  @Path("/user/{userId}/exam/{exam}/subject/{subject}/chapter/{chapter}")
  public List<Mcq> fetchMCQ(@PathParam("userId") Long userId, //
      @PathParam("exam") String exam, //
      @PathParam("subject") String subject, //
      @PathParam("chapter") String chapter) {

    if (userId != null && exam != null && subject != null && chapter != null) {
      return Mcq.findByCreatedByAndExamAndSubjectAndChapter(userId, exam, subject, chapter);
    }

    return null;
  }

  /**
   * {@code POST  /mcqs} : Create a new mcq.
   *
   * @param mcq the mcq to create.
   * @return the {@link Response} with status {@code 201 (Created)} and with body the new mcq, or with status
   *         {@code 400 (Bad Request)} if the mcq has already an ID.
   */
  @POST
  @Transactional
  public Response createMcq(Mcq mcq, @Context UriInfo uriInfo) {

    this.log.debug("REST request to save Mcq : {}", mcq);
    if (mcq.id != null) {
      throw new BadRequestAlertException("A new mcq cannot already have an ID", ENTITY_NAME, "idexists");
    }
    var result = Mcq.persistOrUpdate(mcq);
    var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
    HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code PUT  /mcqs} : Updates an existing mcq.
   *
   * @param mcq the mcq to update.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the updated mcq, or with status
   *         {@code 400 (Bad Request)} if the mcq is not valid, or with status {@code 500 (Internal Server Error)} if
   *         the mcq couldn't be updated.
   */
  @PUT
  @Transactional
  public Response updateMcq(Mcq mcq) {

    this.log.debug("REST request to update Mcq : {}", mcq);
    if (mcq.id == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    var result = Mcq.persistOrUpdate(mcq);
    var response = Response.ok().entity(result);
    HeaderUtil.createEntityUpdateAlert(this.applicationName, true, ENTITY_NAME, mcq.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code DELETE  /mcqs/:id} : delete the "id" mcq.
   *
   * @param id the id of the mcq to delete.
   * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteMcq(@PathParam("id") Long id) {

    this.log.debug("REST request to delete Mcq : {}", id);
    Mcq.findByIdOptional(id).ifPresent(mcq -> {
      mcq.delete();
    });
    var response = Response.noContent();
    HeaderUtil.createEntityDeletionAlert(this.applicationName, true, ENTITY_NAME, id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code GET  /mcqs} : get all the mcqs. * @return the {@link Response} with status {@code 200 (OK)} and the list of
   * mcqs in body.
   */
  @GET
  public List<Mcq> getAllMcqs() {

    this.log.debug("REST request to get all Mcqs");
    return Mcq.findAll().list();
  }

  /**
   * {@code GET  /mcqs/:id} : get the "id" mcq.
   *
   * @param id the id of the mcq to retrieve.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the mcq, or with status
   *         {@code 404 (Not Found)}.
   */
  @GET
  @Path("/{id}")

  public Response getMcq(@PathParam("id") Long id) {

    this.log.debug("REST request to get Mcq : {}", id);
    Optional<Mcq> mcq = Mcq.findByIdOptional(id);
    return ResponseUtil.wrapOrNotFound(mcq);
  }
}

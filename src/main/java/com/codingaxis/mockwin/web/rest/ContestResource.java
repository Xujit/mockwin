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

import com.codingaxis.mockwin.domain.Contest;
import com.codingaxis.mockwin.web.rest.errors.BadRequestAlertException;
import com.codingaxis.mockwin.web.util.HeaderUtil;
import com.codingaxis.mockwin.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.codingaxis.mockwin.domain.Contest}.
 */
@Path("/api/contests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContestResource {

  private final Logger log = LoggerFactory.getLogger(ContestResource.class);

  private static final String ENTITY_NAME = "mockwinfinalContest";

  @ConfigProperty(name = "application.name")
  String applicationName;

  /**
   * {@code POST  /contests} : Create a new contest.
   *
   * @param contest the contest to create.
   * @return the {@link Response} with status {@code 201 (Created)} and with body the new contest, or with status
   *         {@code 400 (Bad Request)} if the contest has already an ID.
   */
  @POST
  @Transactional
  public Response createContest(Contest contest, @Context UriInfo uriInfo) {

    this.log.debug("REST request to save Contest : {}", contest);
    if (contest.id != null) {
      throw new BadRequestAlertException("A new contest cannot already have an ID", ENTITY_NAME, "idexists");
    }
    var result = Contest.persistOrUpdate(contest);
    var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
    HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code PUT  /contests} : Updates an existing contest.
   *
   * @param contest the contest to update.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the updated contest, or with status
   *         {@code 400 (Bad Request)} if the contest is not valid, or with status {@code 500 (Internal Server Error)}
   *         if the contest couldn't be updated.
   */
  @PUT
  @Transactional
  public Response updateContest(Contest contest) {

    this.log.debug("REST request to update Contest : {}", contest);
    if (contest.id == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    var result = Contest.persistOrUpdate(contest);
    var response = Response.ok().entity(result);
    HeaderUtil.createEntityUpdateAlert(this.applicationName, true, ENTITY_NAME, contest.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code DELETE  /contests/:id} : delete the "id" contest.
   *
   * @param id the id of the contest to delete.
   * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteContest(@PathParam("id") Long id) {

    this.log.debug("REST request to delete Contest : {}", id);
    Contest.findByIdOptional(id).ifPresent(contest -> {
      contest.delete();
    });
    var response = Response.noContent();
    HeaderUtil.createEntityDeletionAlert(this.applicationName, true, ENTITY_NAME, id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code GET  /contests} : get all the contests. * @return the {@link Response} with status {@code 200 (OK)} and the
   * list of contests in body.
   */
  @GET
  public List<Contest> getAllContests() {

    this.log.debug("REST request to get all Contests");
    return Contest.findAll().list();
  }

  /**
   * {@code GET  /contests/:id} : get the "id" contest.
   *
   * @param id the id of the contest to retrieve.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the contest, or with status
   *         {@code 404 (Not Found)}.
   */
  @GET
  @Path("/{id}")

  public Response getContest(@PathParam("id") Long id) {

    this.log.debug("REST request to get Contest : {}", id);
    Optional<Contest> contest = Contest.findByIdOptional(id);
    return ResponseUtil.wrapOrNotFound(contest);
  }
}

package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.Competition;
import com.codingaxis.mockwin.web.rest.errors.BadRequestAlertException;
import com.codingaxis.mockwin.web.util.HeaderUtil;
import com.codingaxis.mockwin.web.util.ResponseUtil;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.codingaxis.mockwin.domain.Competition}.
 */
@Path("/api/competitions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CompetitionResource {

    private final Logger log = LoggerFactory.getLogger(CompetitionResource.class);

    private static final String ENTITY_NAME = "mockwinfinalCompetition";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /competitions} : Create a new competition.
     *
     * @param competition the competition to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new competition, or with status {@code 400 (Bad Request)} if the competition has already an ID.
     */
    @POST
    @Transactional
    public Response createCompetition(@Valid Competition competition, @Context UriInfo uriInfo) {
        log.debug("REST request to save Competition : {}", competition);
        if (competition.id != null) {
            throw new BadRequestAlertException("A new competition cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = Competition.persistOrUpdate(competition);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /competitions} : Updates an existing competition.
     *
     * @param competition the competition to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated competition,
     * or with status {@code 400 (Bad Request)} if the competition is not valid,
     * or with status {@code 500 (Internal Server Error)} if the competition couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateCompetition(@Valid Competition competition) {
        log.debug("REST request to update Competition : {}", competition);
        if (competition.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = Competition.persistOrUpdate(competition);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, competition.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /competitions/:id} : delete the "id" competition.
     *
     * @param id the id of the competition to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteCompetition(@PathParam("id") Long id) {
        log.debug("REST request to delete Competition : {}", id);
        Competition.findByIdOptional(id).ifPresent(competition -> {
            competition.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /competitions} : get all the competitions.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of competitions in body.
     */
    @GET
    public List<Competition> getAllCompetitions() {
        log.debug("REST request to get all Competitions");
        return Competition.findAll().list();
    }


    /**
     * {@code GET  /competitions/:id} : get the "id" competition.
     *
     * @param id the id of the competition to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the competition, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getCompetition(@PathParam("id") Long id) {
        log.debug("REST request to get Competition : {}", id);
        Optional<Competition> competition = Competition.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(competition);
    }
}

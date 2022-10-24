package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.CompetExamResponse;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.CompetExamResponse}.
 */
@Path("/api/compet-exam-responses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CompetExamResponseResource {

    private final Logger log = LoggerFactory.getLogger(CompetExamResponseResource.class);

    private static final String ENTITY_NAME = "mockwinfinalCompetExamResponse";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /compet-exam-responses} : Create a new competExamResponse.
     *
     * @param competExamResponse the competExamResponse to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new competExamResponse, or with status {@code 400 (Bad Request)} if the competExamResponse has already an ID.
     */
    @POST
    @Transactional
    public Response createCompetExamResponse(@Valid CompetExamResponse competExamResponse, @Context UriInfo uriInfo) {
        log.debug("REST request to save CompetExamResponse : {}", competExamResponse);
        if (competExamResponse.id != null) {
            throw new BadRequestAlertException("A new competExamResponse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = CompetExamResponse.persistOrUpdate(competExamResponse);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /compet-exam-responses} : Updates an existing competExamResponse.
     *
     * @param competExamResponse the competExamResponse to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated competExamResponse,
     * or with status {@code 400 (Bad Request)} if the competExamResponse is not valid,
     * or with status {@code 500 (Internal Server Error)} if the competExamResponse couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateCompetExamResponse(@Valid CompetExamResponse competExamResponse) {
        log.debug("REST request to update CompetExamResponse : {}", competExamResponse);
        if (competExamResponse.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = CompetExamResponse.persistOrUpdate(competExamResponse);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, competExamResponse.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /compet-exam-responses/:id} : delete the "id" competExamResponse.
     *
     * @param id the id of the competExamResponse to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteCompetExamResponse(@PathParam("id") Long id) {
        log.debug("REST request to delete CompetExamResponse : {}", id);
        CompetExamResponse.findByIdOptional(id).ifPresent(competExamResponse -> {
            competExamResponse.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /compet-exam-responses} : get all the competExamResponses.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of competExamResponses in body.
     */
    @GET
    public List<CompetExamResponse> getAllCompetExamResponses() {
        log.debug("REST request to get all CompetExamResponses");
        return CompetExamResponse.findAll().list();
    }


    /**
     * {@code GET  /compet-exam-responses/:id} : get the "id" competExamResponse.
     *
     * @param id the id of the competExamResponse to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the competExamResponse, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getCompetExamResponse(@PathParam("id") Long id) {
        log.debug("REST request to get CompetExamResponse : {}", id);
        Optional<CompetExamResponse> competExamResponse = CompetExamResponse.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(competExamResponse);
    }
}

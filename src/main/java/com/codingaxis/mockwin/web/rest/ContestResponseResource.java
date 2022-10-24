package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.ContestResponse;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.ContestResponse}.
 */
@Path("/api/contest-responses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContestResponseResource {

    private final Logger log = LoggerFactory.getLogger(ContestResponseResource.class);

    private static final String ENTITY_NAME = "mockwinfinalContestResponse";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /contest-responses} : Create a new contestResponse.
     *
     * @param contestResponse the contestResponse to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new contestResponse, or with status {@code 400 (Bad Request)} if the contestResponse has already an ID.
     */
    @POST
    @Transactional
    public Response createContestResponse(ContestResponse contestResponse, @Context UriInfo uriInfo) {
        log.debug("REST request to save ContestResponse : {}", contestResponse);
        if (contestResponse.id != null) {
            throw new BadRequestAlertException("A new contestResponse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = ContestResponse.persistOrUpdate(contestResponse);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /contest-responses} : Updates an existing contestResponse.
     *
     * @param contestResponse the contestResponse to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated contestResponse,
     * or with status {@code 400 (Bad Request)} if the contestResponse is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contestResponse couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateContestResponse(ContestResponse contestResponse) {
        log.debug("REST request to update ContestResponse : {}", contestResponse);
        if (contestResponse.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = ContestResponse.persistOrUpdate(contestResponse);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contestResponse.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /contest-responses/:id} : delete the "id" contestResponse.
     *
     * @param id the id of the contestResponse to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteContestResponse(@PathParam("id") Long id) {
        log.debug("REST request to delete ContestResponse : {}", id);
        ContestResponse.findByIdOptional(id).ifPresent(contestResponse -> {
            contestResponse.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /contest-responses} : get all the contestResponses.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of contestResponses in body.
     */
    @GET
    public List<ContestResponse> getAllContestResponses() {
        log.debug("REST request to get all ContestResponses");
        return ContestResponse.findAll().list();
    }


    /**
     * {@code GET  /contest-responses/:id} : get the "id" contestResponse.
     *
     * @param id the id of the contestResponse to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the contestResponse, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getContestResponse(@PathParam("id") Long id) {
        log.debug("REST request to get ContestResponse : {}", id);
        Optional<ContestResponse> contestResponse = ContestResponse.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(contestResponse);
    }
}

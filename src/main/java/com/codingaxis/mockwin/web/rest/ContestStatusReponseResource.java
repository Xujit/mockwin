package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.ContestStatusReponse;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.ContestStatusReponse}.
 */
@Path("/api/contest-status-reponses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContestStatusReponseResource {

    private final Logger log = LoggerFactory.getLogger(ContestStatusReponseResource.class);

    private static final String ENTITY_NAME = "mockwinfinalContestStatusReponse";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /contest-status-reponses} : Create a new contestStatusReponse.
     *
     * @param contestStatusReponse the contestStatusReponse to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new contestStatusReponse, or with status {@code 400 (Bad Request)} if the contestStatusReponse has already an ID.
     */
    @POST
    @Transactional
    public Response createContestStatusReponse(ContestStatusReponse contestStatusReponse, @Context UriInfo uriInfo) {
        log.debug("REST request to save ContestStatusReponse : {}", contestStatusReponse);
        if (contestStatusReponse.id != null) {
            throw new BadRequestAlertException("A new contestStatusReponse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = ContestStatusReponse.persistOrUpdate(contestStatusReponse);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /contest-status-reponses} : Updates an existing contestStatusReponse.
     *
     * @param contestStatusReponse the contestStatusReponse to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated contestStatusReponse,
     * or with status {@code 400 (Bad Request)} if the contestStatusReponse is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contestStatusReponse couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateContestStatusReponse(ContestStatusReponse contestStatusReponse) {
        log.debug("REST request to update ContestStatusReponse : {}", contestStatusReponse);
        if (contestStatusReponse.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = ContestStatusReponse.persistOrUpdate(contestStatusReponse);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contestStatusReponse.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /contest-status-reponses/:id} : delete the "id" contestStatusReponse.
     *
     * @param id the id of the contestStatusReponse to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteContestStatusReponse(@PathParam("id") Long id) {
        log.debug("REST request to delete ContestStatusReponse : {}", id);
        ContestStatusReponse.findByIdOptional(id).ifPresent(contestStatusReponse -> {
            contestStatusReponse.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /contest-status-reponses} : get all the contestStatusReponses.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of contestStatusReponses in body.
     */
    @GET
    public List<ContestStatusReponse> getAllContestStatusReponses() {
        log.debug("REST request to get all ContestStatusReponses");
        return ContestStatusReponse.findAll().list();
    }


    /**
     * {@code GET  /contest-status-reponses/:id} : get the "id" contestStatusReponse.
     *
     * @param id the id of the contestStatusReponse to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the contestStatusReponse, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getContestStatusReponse(@PathParam("id") Long id) {
        log.debug("REST request to get ContestStatusReponse : {}", id);
        Optional<ContestStatusReponse> contestStatusReponse = ContestStatusReponse.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(contestStatusReponse);
    }
}

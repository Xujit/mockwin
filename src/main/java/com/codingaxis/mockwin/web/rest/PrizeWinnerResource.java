package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.PrizeWinner;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.PrizeWinner}.
 */
@Path("/api/prize-winners")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PrizeWinnerResource {

    private final Logger log = LoggerFactory.getLogger(PrizeWinnerResource.class);

    private static final String ENTITY_NAME = "mockwinfinalPrizeWinner";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /prize-winners} : Create a new prizeWinner.
     *
     * @param prizeWinner the prizeWinner to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new prizeWinner, or with status {@code 400 (Bad Request)} if the prizeWinner has already an ID.
     */
    @POST
    @Transactional
    public Response createPrizeWinner(@Valid PrizeWinner prizeWinner, @Context UriInfo uriInfo) {
        log.debug("REST request to save PrizeWinner : {}", prizeWinner);
        if (prizeWinner.id != null) {
            throw new BadRequestAlertException("A new prizeWinner cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = PrizeWinner.persistOrUpdate(prizeWinner);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /prize-winners} : Updates an existing prizeWinner.
     *
     * @param prizeWinner the prizeWinner to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated prizeWinner,
     * or with status {@code 400 (Bad Request)} if the prizeWinner is not valid,
     * or with status {@code 500 (Internal Server Error)} if the prizeWinner couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updatePrizeWinner(@Valid PrizeWinner prizeWinner) {
        log.debug("REST request to update PrizeWinner : {}", prizeWinner);
        if (prizeWinner.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = PrizeWinner.persistOrUpdate(prizeWinner);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, prizeWinner.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /prize-winners/:id} : delete the "id" prizeWinner.
     *
     * @param id the id of the prizeWinner to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletePrizeWinner(@PathParam("id") Long id) {
        log.debug("REST request to delete PrizeWinner : {}", id);
        PrizeWinner.findByIdOptional(id).ifPresent(prizeWinner -> {
            prizeWinner.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /prize-winners} : get all the prizeWinners.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of prizeWinners in body.
     */
    @GET
    public List<PrizeWinner> getAllPrizeWinners() {
        log.debug("REST request to get all PrizeWinners");
        return PrizeWinner.findAll().list();
    }


    /**
     * {@code GET  /prize-winners/:id} : get the "id" prizeWinner.
     *
     * @param id the id of the prizeWinner to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the prizeWinner, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getPrizeWinner(@PathParam("id") Long id) {
        log.debug("REST request to get PrizeWinner : {}", id);
        Optional<PrizeWinner> prizeWinner = PrizeWinner.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(prizeWinner);
    }
}

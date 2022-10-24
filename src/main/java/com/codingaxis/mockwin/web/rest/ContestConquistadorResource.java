package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.ContestConquistador;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.ContestConquistador}.
 */
@Path("/api/contest-conquistadors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContestConquistadorResource {

    private final Logger log = LoggerFactory.getLogger(ContestConquistadorResource.class);

    private static final String ENTITY_NAME = "mockwinfinalContestConquistador";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /contest-conquistadors} : Create a new contestConquistador.
     *
     * @param contestConquistador the contestConquistador to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new contestConquistador, or with status {@code 400 (Bad Request)} if the contestConquistador has already an ID.
     */
    @POST
    @Transactional
    public Response createContestConquistador(ContestConquistador contestConquistador, @Context UriInfo uriInfo) {
        log.debug("REST request to save ContestConquistador : {}", contestConquistador);
        if (contestConquistador.id != null) {
            throw new BadRequestAlertException("A new contestConquistador cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = ContestConquistador.persistOrUpdate(contestConquistador);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /contest-conquistadors} : Updates an existing contestConquistador.
     *
     * @param contestConquistador the contestConquistador to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated contestConquistador,
     * or with status {@code 400 (Bad Request)} if the contestConquistador is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contestConquistador couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateContestConquistador(ContestConquistador contestConquistador) {
        log.debug("REST request to update ContestConquistador : {}", contestConquistador);
        if (contestConquistador.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = ContestConquistador.persistOrUpdate(contestConquistador);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contestConquistador.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /contest-conquistadors/:id} : delete the "id" contestConquistador.
     *
     * @param id the id of the contestConquistador to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteContestConquistador(@PathParam("id") Long id) {
        log.debug("REST request to delete ContestConquistador : {}", id);
        ContestConquistador.findByIdOptional(id).ifPresent(contestConquistador -> {
            contestConquistador.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /contest-conquistadors} : get all the contestConquistadors.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of contestConquistadors in body.
     */
    @GET
    public List<ContestConquistador> getAllContestConquistadors() {
        log.debug("REST request to get all ContestConquistadors");
        return ContestConquistador.findAll().list();
    }


    /**
     * {@code GET  /contest-conquistadors/:id} : get the "id" contestConquistador.
     *
     * @param id the id of the contestConquistador to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the contestConquistador, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getContestConquistador(@PathParam("id") Long id) {
        log.debug("REST request to get ContestConquistador : {}", id);
        Optional<ContestConquistador> contestConquistador = ContestConquistador.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(contestConquistador);
    }
}

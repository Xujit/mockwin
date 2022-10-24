package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.ContestSme;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.ContestSme}.
 */
@Path("/api/contest-smes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContestSmeResource {

    private final Logger log = LoggerFactory.getLogger(ContestSmeResource.class);

    private static final String ENTITY_NAME = "mockwinfinalContestSme";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /contest-smes} : Create a new contestSme.
     *
     * @param contestSme the contestSme to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new contestSme, or with status {@code 400 (Bad Request)} if the contestSme has already an ID.
     */
    @POST
    @Transactional
    public Response createContestSme(ContestSme contestSme, @Context UriInfo uriInfo) {
        log.debug("REST request to save ContestSme : {}", contestSme);
        if (contestSme.id != null) {
            throw new BadRequestAlertException("A new contestSme cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = ContestSme.persistOrUpdate(contestSme);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /contest-smes} : Updates an existing contestSme.
     *
     * @param contestSme the contestSme to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated contestSme,
     * or with status {@code 400 (Bad Request)} if the contestSme is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contestSme couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateContestSme(ContestSme contestSme) {
        log.debug("REST request to update ContestSme : {}", contestSme);
        if (contestSme.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = ContestSme.persistOrUpdate(contestSme);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contestSme.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /contest-smes/:id} : delete the "id" contestSme.
     *
     * @param id the id of the contestSme to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteContestSme(@PathParam("id") Long id) {
        log.debug("REST request to delete ContestSme : {}", id);
        ContestSme.findByIdOptional(id).ifPresent(contestSme -> {
            contestSme.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /contest-smes} : get all the contestSmes.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of contestSmes in body.
     */
    @GET
    public List<ContestSme> getAllContestSmes() {
        log.debug("REST request to get all ContestSmes");
        return ContestSme.findAll().list();
    }


    /**
     * {@code GET  /contest-smes/:id} : get the "id" contestSme.
     *
     * @param id the id of the contestSme to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the contestSme, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getContestSme(@PathParam("id") Long id) {
        log.debug("REST request to get ContestSme : {}", id);
        Optional<ContestSme> contestSme = ContestSme.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(contestSme);
    }
}

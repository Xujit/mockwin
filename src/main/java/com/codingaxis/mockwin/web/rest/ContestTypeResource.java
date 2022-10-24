package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.ContestType;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.ContestType}.
 */
@Path("/api/contest-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContestTypeResource {

    private final Logger log = LoggerFactory.getLogger(ContestTypeResource.class);

    private static final String ENTITY_NAME = "mockwinfinalContestType";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /contest-types} : Create a new contestType.
     *
     * @param contestType the contestType to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new contestType, or with status {@code 400 (Bad Request)} if the contestType has already an ID.
     */
    @POST
    @Transactional
    public Response createContestType(@Valid ContestType contestType, @Context UriInfo uriInfo) {
        log.debug("REST request to save ContestType : {}", contestType);
        if (contestType.id != null) {
            throw new BadRequestAlertException("A new contestType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = ContestType.persistOrUpdate(contestType);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /contest-types} : Updates an existing contestType.
     *
     * @param contestType the contestType to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated contestType,
     * or with status {@code 400 (Bad Request)} if the contestType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contestType couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateContestType(@Valid ContestType contestType) {
        log.debug("REST request to update ContestType : {}", contestType);
        if (contestType.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = ContestType.persistOrUpdate(contestType);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contestType.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /contest-types/:id} : delete the "id" contestType.
     *
     * @param id the id of the contestType to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteContestType(@PathParam("id") Long id) {
        log.debug("REST request to delete ContestType : {}", id);
        ContestType.findByIdOptional(id).ifPresent(contestType -> {
            contestType.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /contest-types} : get all the contestTypes.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of contestTypes in body.
     */
    @GET
    public List<ContestType> getAllContestTypes() {
        log.debug("REST request to get all ContestTypes");
        return ContestType.findAll().list();
    }


    /**
     * {@code GET  /contest-types/:id} : get the "id" contestType.
     *
     * @param id the id of the contestType to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the contestType, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getContestType(@PathParam("id") Long id) {
        log.debug("REST request to get ContestType : {}", id);
        Optional<ContestType> contestType = ContestType.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(contestType);
    }
}

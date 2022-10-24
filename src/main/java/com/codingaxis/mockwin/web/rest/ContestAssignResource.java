package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.ContestAssign;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.ContestAssign}.
 */
@Path("/api/contest-assigns")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContestAssignResource {

    private final Logger log = LoggerFactory.getLogger(ContestAssignResource.class);

    private static final String ENTITY_NAME = "mockwinfinalContestAssign";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /contest-assigns} : Create a new contestAssign.
     *
     * @param contestAssign the contestAssign to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new contestAssign, or with status {@code 400 (Bad Request)} if the contestAssign has already an ID.
     */
    @POST
    @Transactional
    public Response createContestAssign(ContestAssign contestAssign, @Context UriInfo uriInfo) {
        log.debug("REST request to save ContestAssign : {}", contestAssign);
        if (contestAssign.id != null) {
            throw new BadRequestAlertException("A new contestAssign cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = ContestAssign.persistOrUpdate(contestAssign);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /contest-assigns} : Updates an existing contestAssign.
     *
     * @param contestAssign the contestAssign to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated contestAssign,
     * or with status {@code 400 (Bad Request)} if the contestAssign is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contestAssign couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateContestAssign(ContestAssign contestAssign) {
        log.debug("REST request to update ContestAssign : {}", contestAssign);
        if (contestAssign.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = ContestAssign.persistOrUpdate(contestAssign);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contestAssign.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /contest-assigns/:id} : delete the "id" contestAssign.
     *
     * @param id the id of the contestAssign to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteContestAssign(@PathParam("id") Long id) {
        log.debug("REST request to delete ContestAssign : {}", id);
        ContestAssign.findByIdOptional(id).ifPresent(contestAssign -> {
            contestAssign.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /contest-assigns} : get all the contestAssigns.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of contestAssigns in body.
     */
    @GET
    public List<ContestAssign> getAllContestAssigns() {
        log.debug("REST request to get all ContestAssigns");
        return ContestAssign.findAll().list();
    }


    /**
     * {@code GET  /contest-assigns/:id} : get the "id" contestAssign.
     *
     * @param id the id of the contestAssign to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the contestAssign, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getContestAssign(@PathParam("id") Long id) {
        log.debug("REST request to get ContestAssign : {}", id);
        Optional<ContestAssign> contestAssign = ContestAssign.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(contestAssign);
    }
}

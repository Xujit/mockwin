package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.SubContest;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.SubContest}.
 */
@Path("/api/sub-contests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class SubContestResource {

    private final Logger log = LoggerFactory.getLogger(SubContestResource.class);

    private static final String ENTITY_NAME = "mockwinfinalSubContest";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /sub-contests} : Create a new subContest.
     *
     * @param subContest the subContest to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new subContest, or with status {@code 400 (Bad Request)} if the subContest has already an ID.
     */
    @POST
    @Transactional
    public Response createSubContest(SubContest subContest, @Context UriInfo uriInfo) {
        log.debug("REST request to save SubContest : {}", subContest);
        if (subContest.id != null) {
            throw new BadRequestAlertException("A new subContest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = SubContest.persistOrUpdate(subContest);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /sub-contests} : Updates an existing subContest.
     *
     * @param subContest the subContest to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated subContest,
     * or with status {@code 400 (Bad Request)} if the subContest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subContest couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateSubContest(SubContest subContest) {
        log.debug("REST request to update SubContest : {}", subContest);
        if (subContest.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = SubContest.persistOrUpdate(subContest);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subContest.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /sub-contests/:id} : delete the "id" subContest.
     *
     * @param id the id of the subContest to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteSubContest(@PathParam("id") Long id) {
        log.debug("REST request to delete SubContest : {}", id);
        SubContest.findByIdOptional(id).ifPresent(subContest -> {
            subContest.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /sub-contests} : get all the subContests.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of subContests in body.
     */
    @GET
    public List<SubContest> getAllSubContests() {
        log.debug("REST request to get all SubContests");
        return SubContest.findAll().list();
    }


    /**
     * {@code GET  /sub-contests/:id} : get the "id" subContest.
     *
     * @param id the id of the subContest to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the subContest, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getSubContest(@PathParam("id") Long id) {
        log.debug("REST request to get SubContest : {}", id);
        Optional<SubContest> subContest = SubContest.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(subContest);
    }
}

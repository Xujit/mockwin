package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.MCQContest;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.MCQContest}.
 */
@Path("/api/mcq-contests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class MCQContestResource {

    private final Logger log = LoggerFactory.getLogger(MCQContestResource.class);

    private static final String ENTITY_NAME = "mockwinfinalMcqContest";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /mcq-contests} : Create a new mCQContest.
     *
     * @param mCQContest the mCQContest to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new mCQContest, or with status {@code 400 (Bad Request)} if the mCQContest has already an ID.
     */
    @POST
    @Transactional
    public Response createMCQContest(MCQContest mCQContest, @Context UriInfo uriInfo) {
        log.debug("REST request to save MCQContest : {}", mCQContest);
        if (mCQContest.id != null) {
            throw new BadRequestAlertException("A new mCQContest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = MCQContest.persistOrUpdate(mCQContest);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /mcq-contests} : Updates an existing mCQContest.
     *
     * @param mCQContest the mCQContest to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated mCQContest,
     * or with status {@code 400 (Bad Request)} if the mCQContest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mCQContest couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateMCQContest(MCQContest mCQContest) {
        log.debug("REST request to update MCQContest : {}", mCQContest);
        if (mCQContest.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = MCQContest.persistOrUpdate(mCQContest);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mCQContest.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /mcq-contests/:id} : delete the "id" mCQContest.
     *
     * @param id the id of the mCQContest to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteMCQContest(@PathParam("id") Long id) {
        log.debug("REST request to delete MCQContest : {}", id);
        MCQContest.findByIdOptional(id).ifPresent(mCQContest -> {
            mCQContest.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /mcq-contests} : get all the mCQContests.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of mCQContests in body.
     */
    @GET
    public List<MCQContest> getAllMCQContests() {
        log.debug("REST request to get all MCQContests");
        return MCQContest.findAll().list();
    }


    /**
     * {@code GET  /mcq-contests/:id} : get the "id" mCQContest.
     *
     * @param id the id of the mCQContest to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the mCQContest, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getMCQContest(@PathParam("id") Long id) {
        log.debug("REST request to get MCQContest : {}", id);
        Optional<MCQContest> mCQContest = MCQContest.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(mCQContest);
    }
}

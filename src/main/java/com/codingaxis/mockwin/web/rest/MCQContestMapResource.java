package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.MCQContestMap;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.MCQContestMap}.
 */
@Path("/api/mcq-contest-maps")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class MCQContestMapResource {

    private final Logger log = LoggerFactory.getLogger(MCQContestMapResource.class);

    private static final String ENTITY_NAME = "mockwinfinalMcqContestMap";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /mcq-contest-maps} : Create a new mCQContestMap.
     *
     * @param mCQContestMap the mCQContestMap to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new mCQContestMap, or with status {@code 400 (Bad Request)} if the mCQContestMap has already an ID.
     */
    @POST
    @Transactional
    public Response createMCQContestMap(MCQContestMap mCQContestMap, @Context UriInfo uriInfo) {
        log.debug("REST request to save MCQContestMap : {}", mCQContestMap);
        if (mCQContestMap.id != null) {
            throw new BadRequestAlertException("A new mCQContestMap cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = MCQContestMap.persistOrUpdate(mCQContestMap);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /mcq-contest-maps} : Updates an existing mCQContestMap.
     *
     * @param mCQContestMap the mCQContestMap to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated mCQContestMap,
     * or with status {@code 400 (Bad Request)} if the mCQContestMap is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mCQContestMap couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateMCQContestMap(MCQContestMap mCQContestMap) {
        log.debug("REST request to update MCQContestMap : {}", mCQContestMap);
        if (mCQContestMap.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = MCQContestMap.persistOrUpdate(mCQContestMap);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mCQContestMap.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /mcq-contest-maps/:id} : delete the "id" mCQContestMap.
     *
     * @param id the id of the mCQContestMap to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteMCQContestMap(@PathParam("id") Long id) {
        log.debug("REST request to delete MCQContestMap : {}", id);
        MCQContestMap.findByIdOptional(id).ifPresent(mCQContestMap -> {
            mCQContestMap.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /mcq-contest-maps} : get all the mCQContestMaps.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of mCQContestMaps in body.
     */
    @GET
    public List<MCQContestMap> getAllMCQContestMaps() {
        log.debug("REST request to get all MCQContestMaps");
        return MCQContestMap.findAll().list();
    }


    /**
     * {@code GET  /mcq-contest-maps/:id} : get the "id" mCQContestMap.
     *
     * @param id the id of the mCQContestMap to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the mCQContestMap, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getMCQContestMap(@PathParam("id") Long id) {
        log.debug("REST request to get MCQContestMap : {}", id);
        Optional<MCQContestMap> mCQContestMap = MCQContestMap.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(mCQContestMap);
    }
}

package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.BannerContest;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.BannerContest}.
 */
@Path("/api/banner-contests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class BannerContestResource {

    private final Logger log = LoggerFactory.getLogger(BannerContestResource.class);

    private static final String ENTITY_NAME = "mockwinfinalBannerContest";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /banner-contests} : Create a new bannerContest.
     *
     * @param bannerContest the bannerContest to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new bannerContest, or with status {@code 400 (Bad Request)} if the bannerContest has already an ID.
     */
    @POST
    @Transactional
    public Response createBannerContest(@Valid BannerContest bannerContest, @Context UriInfo uriInfo) {
        log.debug("REST request to save BannerContest : {}", bannerContest);
        if (bannerContest.id != null) {
            throw new BadRequestAlertException("A new bannerContest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = BannerContest.persistOrUpdate(bannerContest);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /banner-contests} : Updates an existing bannerContest.
     *
     * @param bannerContest the bannerContest to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated bannerContest,
     * or with status {@code 400 (Bad Request)} if the bannerContest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bannerContest couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateBannerContest(@Valid BannerContest bannerContest) {
        log.debug("REST request to update BannerContest : {}", bannerContest);
        if (bannerContest.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = BannerContest.persistOrUpdate(bannerContest);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bannerContest.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /banner-contests/:id} : delete the "id" bannerContest.
     *
     * @param id the id of the bannerContest to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteBannerContest(@PathParam("id") Long id) {
        log.debug("REST request to delete BannerContest : {}", id);
        BannerContest.findByIdOptional(id).ifPresent(bannerContest -> {
            bannerContest.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /banner-contests} : get all the bannerContests.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of bannerContests in body.
     */
    @GET
    public List<BannerContest> getAllBannerContests() {
        log.debug("REST request to get all BannerContests");
        return BannerContest.findAll().list();
    }


    /**
     * {@code GET  /banner-contests/:id} : get the "id" bannerContest.
     *
     * @param id the id of the bannerContest to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the bannerContest, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getBannerContest(@PathParam("id") Long id) {
        log.debug("REST request to get BannerContest : {}", id);
        Optional<BannerContest> bannerContest = BannerContest.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(bannerContest);
    }
}

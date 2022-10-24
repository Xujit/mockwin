package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.ContestCategory;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.ContestCategory}.
 */
@Path("/api/contest-categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContestCategoryResource {

    private final Logger log = LoggerFactory.getLogger(ContestCategoryResource.class);

    private static final String ENTITY_NAME = "mockwinfinalContestCategory";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /contest-categories} : Create a new contestCategory.
     *
     * @param contestCategory the contestCategory to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new contestCategory, or with status {@code 400 (Bad Request)} if the contestCategory has already an ID.
     */
    @POST
    @Transactional
    public Response createContestCategory(ContestCategory contestCategory, @Context UriInfo uriInfo) {
        log.debug("REST request to save ContestCategory : {}", contestCategory);
        if (contestCategory.id != null) {
            throw new BadRequestAlertException("A new contestCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = ContestCategory.persistOrUpdate(contestCategory);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /contest-categories} : Updates an existing contestCategory.
     *
     * @param contestCategory the contestCategory to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated contestCategory,
     * or with status {@code 400 (Bad Request)} if the contestCategory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contestCategory couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateContestCategory(ContestCategory contestCategory) {
        log.debug("REST request to update ContestCategory : {}", contestCategory);
        if (contestCategory.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = ContestCategory.persistOrUpdate(contestCategory);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contestCategory.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /contest-categories/:id} : delete the "id" contestCategory.
     *
     * @param id the id of the contestCategory to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteContestCategory(@PathParam("id") Long id) {
        log.debug("REST request to delete ContestCategory : {}", id);
        ContestCategory.findByIdOptional(id).ifPresent(contestCategory -> {
            contestCategory.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /contest-categories} : get all the contestCategories.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of contestCategories in body.
     */
    @GET
    public List<ContestCategory> getAllContestCategories() {
        log.debug("REST request to get all ContestCategories");
        return ContestCategory.findAll().list();
    }


    /**
     * {@code GET  /contest-categories/:id} : get the "id" contestCategory.
     *
     * @param id the id of the contestCategory to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the contestCategory, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getContestCategory(@PathParam("id") Long id) {
        log.debug("REST request to get ContestCategory : {}", id);
        Optional<ContestCategory> contestCategory = ContestCategory.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(contestCategory);
    }
}

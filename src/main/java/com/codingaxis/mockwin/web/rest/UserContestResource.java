package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.UserContest;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.UserContest}.
 */
@Path("/api/user-contests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserContestResource {

    private final Logger log = LoggerFactory.getLogger(UserContestResource.class);

    private static final String ENTITY_NAME = "mockwinfinalUserContest";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /user-contests} : Create a new userContest.
     *
     * @param userContest the userContest to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new userContest, or with status {@code 400 (Bad Request)} if the userContest has already an ID.
     */
    @POST
    @Transactional
    public Response createUserContest(UserContest userContest, @Context UriInfo uriInfo) {
        log.debug("REST request to save UserContest : {}", userContest);
        if (userContest.id != null) {
            throw new BadRequestAlertException("A new userContest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = UserContest.persistOrUpdate(userContest);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /user-contests} : Updates an existing userContest.
     *
     * @param userContest the userContest to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated userContest,
     * or with status {@code 400 (Bad Request)} if the userContest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userContest couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateUserContest(UserContest userContest) {
        log.debug("REST request to update UserContest : {}", userContest);
        if (userContest.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = UserContest.persistOrUpdate(userContest);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userContest.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /user-contests/:id} : delete the "id" userContest.
     *
     * @param id the id of the userContest to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUserContest(@PathParam("id") Long id) {
        log.debug("REST request to delete UserContest : {}", id);
        UserContest.findByIdOptional(id).ifPresent(userContest -> {
            userContest.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /user-contests} : get all the userContests.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of userContests in body.
     */
    @GET
    public List<UserContest> getAllUserContests() {
        log.debug("REST request to get all UserContests");
        return UserContest.findAll().list();
    }


    /**
     * {@code GET  /user-contests/:id} : get the "id" userContest.
     *
     * @param id the id of the userContest to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the userContest, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getUserContest(@PathParam("id") Long id) {
        log.debug("REST request to get UserContest : {}", id);
        Optional<UserContest> userContest = UserContest.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(userContest);
    }
}

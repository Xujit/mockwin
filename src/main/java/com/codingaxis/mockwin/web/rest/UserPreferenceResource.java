package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.UserPreference;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.UserPreference}.
 */
@Path("/api/user-preferences")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserPreferenceResource {

    private final Logger log = LoggerFactory.getLogger(UserPreferenceResource.class);

    private static final String ENTITY_NAME = "mockwinfinalUserPreference";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /user-preferences} : Create a new userPreference.
     *
     * @param userPreference the userPreference to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new userPreference, or with status {@code 400 (Bad Request)} if the userPreference has already an ID.
     */
    @POST
    @Transactional
    public Response createUserPreference(UserPreference userPreference, @Context UriInfo uriInfo) {
        log.debug("REST request to save UserPreference : {}", userPreference);
        if (userPreference.id != null) {
            throw new BadRequestAlertException("A new userPreference cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = UserPreference.persistOrUpdate(userPreference);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /user-preferences} : Updates an existing userPreference.
     *
     * @param userPreference the userPreference to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated userPreference,
     * or with status {@code 400 (Bad Request)} if the userPreference is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userPreference couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateUserPreference(UserPreference userPreference) {
        log.debug("REST request to update UserPreference : {}", userPreference);
        if (userPreference.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = UserPreference.persistOrUpdate(userPreference);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userPreference.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /user-preferences/:id} : delete the "id" userPreference.
     *
     * @param id the id of the userPreference to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUserPreference(@PathParam("id") Long id) {
        log.debug("REST request to delete UserPreference : {}", id);
        UserPreference.findByIdOptional(id).ifPresent(userPreference -> {
            userPreference.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /user-preferences} : get all the userPreferences.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of userPreferences in body.
     */
    @GET
    public List<UserPreference> getAllUserPreferences() {
        log.debug("REST request to get all UserPreferences");
        return UserPreference.findAll().list();
    }


    /**
     * {@code GET  /user-preferences/:id} : get the "id" userPreference.
     *
     * @param id the id of the userPreference to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the userPreference, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getUserPreference(@PathParam("id") Long id) {
        log.debug("REST request to get UserPreference : {}", id);
        Optional<UserPreference> userPreference = UserPreference.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(userPreference);
    }
}

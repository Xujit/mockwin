package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.UserNotification;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.UserNotification}.
 */
@Path("/api/user-notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserNotificationResource {

    private final Logger log = LoggerFactory.getLogger(UserNotificationResource.class);

    private static final String ENTITY_NAME = "mockwinfinalUserNotification";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /user-notifications} : Create a new userNotification.
     *
     * @param userNotification the userNotification to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new userNotification, or with status {@code 400 (Bad Request)} if the userNotification has already an ID.
     */
    @POST
    @Transactional
    public Response createUserNotification(UserNotification userNotification, @Context UriInfo uriInfo) {
        log.debug("REST request to save UserNotification : {}", userNotification);
        if (userNotification.id != null) {
            throw new BadRequestAlertException("A new userNotification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = UserNotification.persistOrUpdate(userNotification);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /user-notifications} : Updates an existing userNotification.
     *
     * @param userNotification the userNotification to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated userNotification,
     * or with status {@code 400 (Bad Request)} if the userNotification is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userNotification couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateUserNotification(UserNotification userNotification) {
        log.debug("REST request to update UserNotification : {}", userNotification);
        if (userNotification.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = UserNotification.persistOrUpdate(userNotification);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userNotification.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /user-notifications/:id} : delete the "id" userNotification.
     *
     * @param id the id of the userNotification to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUserNotification(@PathParam("id") Long id) {
        log.debug("REST request to delete UserNotification : {}", id);
        UserNotification.findByIdOptional(id).ifPresent(userNotification -> {
            userNotification.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /user-notifications} : get all the userNotifications.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of userNotifications in body.
     */
    @GET
    public List<UserNotification> getAllUserNotifications() {
        log.debug("REST request to get all UserNotifications");
        return UserNotification.findAll().list();
    }


    /**
     * {@code GET  /user-notifications/:id} : get the "id" userNotification.
     *
     * @param id the id of the userNotification to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the userNotification, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getUserNotification(@PathParam("id") Long id) {
        log.debug("REST request to get UserNotification : {}", id);
        Optional<UserNotification> userNotification = UserNotification.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(userNotification);
    }
}

package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.UserType;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.UserType}.
 */
@Path("/api/user-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserTypeResource {

    private final Logger log = LoggerFactory.getLogger(UserTypeResource.class);

    private static final String ENTITY_NAME = "mockwinfinalUserType";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /user-types} : Create a new userType.
     *
     * @param userType the userType to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new userType, or with status {@code 400 (Bad Request)} if the userType has already an ID.
     */
    @POST
    @Transactional
    public Response createUserType(UserType userType, @Context UriInfo uriInfo) {
        log.debug("REST request to save UserType : {}", userType);
        if (userType.id != null) {
            throw new BadRequestAlertException("A new userType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = UserType.persistOrUpdate(userType);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /user-types} : Updates an existing userType.
     *
     * @param userType the userType to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated userType,
     * or with status {@code 400 (Bad Request)} if the userType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userType couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateUserType(UserType userType) {
        log.debug("REST request to update UserType : {}", userType);
        if (userType.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = UserType.persistOrUpdate(userType);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userType.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /user-types/:id} : delete the "id" userType.
     *
     * @param id the id of the userType to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUserType(@PathParam("id") Long id) {
        log.debug("REST request to delete UserType : {}", id);
        UserType.findByIdOptional(id).ifPresent(userType -> {
            userType.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /user-types} : get all the userTypes.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of userTypes in body.
     */
    @GET
    public List<UserType> getAllUserTypes() {
        log.debug("REST request to get all UserTypes");
        return UserType.findAll().list();
    }


    /**
     * {@code GET  /user-types/:id} : get the "id" userType.
     *
     * @param id the id of the userType to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the userType, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getUserType(@PathParam("id") Long id) {
        log.debug("REST request to get UserType : {}", id);
        Optional<UserType> userType = UserType.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(userType);
    }
}

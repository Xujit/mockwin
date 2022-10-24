package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.Assign;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.Assign}.
 */
@Path("/api/assigns")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AssignResource {

    private final Logger log = LoggerFactory.getLogger(AssignResource.class);

    private static final String ENTITY_NAME = "mockwinfinalAssign";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /assigns} : Create a new assign.
     *
     * @param assign the assign to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new assign, or with status {@code 400 (Bad Request)} if the assign has already an ID.
     */
    @POST
    @Transactional
    public Response createAssign(@Valid Assign assign, @Context UriInfo uriInfo) {
        log.debug("REST request to save Assign : {}", assign);
        if (assign.id != null) {
            throw new BadRequestAlertException("A new assign cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = Assign.persistOrUpdate(assign);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /assigns} : Updates an existing assign.
     *
     * @param assign the assign to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated assign,
     * or with status {@code 400 (Bad Request)} if the assign is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assign couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateAssign(@Valid Assign assign) {
        log.debug("REST request to update Assign : {}", assign);
        if (assign.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = Assign.persistOrUpdate(assign);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assign.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /assigns/:id} : delete the "id" assign.
     *
     * @param id the id of the assign to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteAssign(@PathParam("id") Long id) {
        log.debug("REST request to delete Assign : {}", id);
        Assign.findByIdOptional(id).ifPresent(assign -> {
            assign.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /assigns} : get all the assigns.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of assigns in body.
     */
    @GET
    public List<Assign> getAllAssigns() {
        log.debug("REST request to get all Assigns");
        return Assign.findAll().list();
    }


    /**
     * {@code GET  /assigns/:id} : get the "id" assign.
     *
     * @param id the id of the assign to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the assign, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getAssign(@PathParam("id") Long id) {
        log.debug("REST request to get Assign : {}", id);
        Optional<Assign> assign = Assign.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(assign);
    }
}

package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.Options;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.Options}.
 */
@Path("/api/options")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class OptionsResource {

    private final Logger log = LoggerFactory.getLogger(OptionsResource.class);

    private static final String ENTITY_NAME = "mockwinfinalOptions";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /options} : Create a new options.
     *
     * @param options the options to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new options, or with status {@code 400 (Bad Request)} if the options has already an ID.
     */
    @POST
    @Transactional
    public Response createOptions(@Valid Options options, @Context UriInfo uriInfo) {
        log.debug("REST request to save Options : {}", options);
        if (options.id != null) {
            throw new BadRequestAlertException("A new options cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = Options.persistOrUpdate(options);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /options} : Updates an existing options.
     *
     * @param options the options to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated options,
     * or with status {@code 400 (Bad Request)} if the options is not valid,
     * or with status {@code 500 (Internal Server Error)} if the options couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateOptions(@Valid Options options) {
        log.debug("REST request to update Options : {}", options);
        if (options.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = Options.persistOrUpdate(options);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, options.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /options/:id} : delete the "id" options.
     *
     * @param id the id of the options to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteOptions(@PathParam("id") Long id) {
        log.debug("REST request to delete Options : {}", id);
        Options.findByIdOptional(id).ifPresent(options -> {
            options.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /options} : get all the options.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of options in body.
     */
    @GET
    public List<Options> getAllOptions() {
        log.debug("REST request to get all Options");
        return Options.findAll().list();
    }


    /**
     * {@code GET  /options/:id} : get the "id" options.
     *
     * @param id the id of the options to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the options, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getOptions(@PathParam("id") Long id) {
        log.debug("REST request to get Options : {}", id);
        Optional<Options> options = Options.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(options);
    }
}

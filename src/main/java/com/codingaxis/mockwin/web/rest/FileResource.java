package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.File;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.File}.
 */
@Path("/api/files")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class FileResource {

    private final Logger log = LoggerFactory.getLogger(FileResource.class);

    private static final String ENTITY_NAME = "mockwinfinalFile";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /files} : Create a new file.
     *
     * @param file the file to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new file, or with status {@code 400 (Bad Request)} if the file has already an ID.
     */
    @POST
    @Transactional
    public Response createFile(File file, @Context UriInfo uriInfo) {
        log.debug("REST request to save File : {}", file);
        if (file.id != null) {
            throw new BadRequestAlertException("A new file cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = File.persistOrUpdate(file);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /files} : Updates an existing file.
     *
     * @param file the file to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated file,
     * or with status {@code 400 (Bad Request)} if the file is not valid,
     * or with status {@code 500 (Internal Server Error)} if the file couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateFile(File file) {
        log.debug("REST request to update File : {}", file);
        if (file.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = File.persistOrUpdate(file);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, file.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /files/:id} : delete the "id" file.
     *
     * @param id the id of the file to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteFile(@PathParam("id") Long id) {
        log.debug("REST request to delete File : {}", id);
        File.findByIdOptional(id).ifPresent(file -> {
            file.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /files} : get all the files.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of files in body.
     */
    @GET
    public List<File> getAllFiles() {
        log.debug("REST request to get all Files");
        return File.findAll().list();
    }


    /**
     * {@code GET  /files/:id} : get the "id" file.
     *
     * @param id the id of the file to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the file, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getFile(@PathParam("id") Long id) {
        log.debug("REST request to get File : {}", id);
        Optional<File> file = File.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(file);
    }
}

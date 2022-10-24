package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.Curriculum;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.Curriculum}.
 */
@Path("/api/curricula")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CurriculumResource {

    private final Logger log = LoggerFactory.getLogger(CurriculumResource.class);

    private static final String ENTITY_NAME = "mockwinfinalCurriculum";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /curricula} : Create a new curriculum.
     *
     * @param curriculum the curriculum to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new curriculum, or with status {@code 400 (Bad Request)} if the curriculum has already an ID.
     */
    @POST
    @Transactional
    public Response createCurriculum(Curriculum curriculum, @Context UriInfo uriInfo) {
        log.debug("REST request to save Curriculum : {}", curriculum);
        if (curriculum.id != null) {
            throw new BadRequestAlertException("A new curriculum cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = Curriculum.persistOrUpdate(curriculum);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /curricula} : Updates an existing curriculum.
     *
     * @param curriculum the curriculum to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated curriculum,
     * or with status {@code 400 (Bad Request)} if the curriculum is not valid,
     * or with status {@code 500 (Internal Server Error)} if the curriculum couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateCurriculum(Curriculum curriculum) {
        log.debug("REST request to update Curriculum : {}", curriculum);
        if (curriculum.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = Curriculum.persistOrUpdate(curriculum);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, curriculum.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /curricula/:id} : delete the "id" curriculum.
     *
     * @param id the id of the curriculum to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteCurriculum(@PathParam("id") Long id) {
        log.debug("REST request to delete Curriculum : {}", id);
        Curriculum.findByIdOptional(id).ifPresent(curriculum -> {
            curriculum.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /curricula} : get all the curricula.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of curricula in body.
     */
    @GET
    public List<Curriculum> getAllCurricula() {
        log.debug("REST request to get all Curricula");
        return Curriculum.findAll().list();
    }


    /**
     * {@code GET  /curricula/:id} : get the "id" curriculum.
     *
     * @param id the id of the curriculum to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the curriculum, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getCurriculum(@PathParam("id") Long id) {
        log.debug("REST request to get Curriculum : {}", id);
        Optional<Curriculum> curriculum = Curriculum.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(curriculum);
    }
}

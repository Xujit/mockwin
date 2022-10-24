package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.codingaxis.mockwin.domain.CompetExam;
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
 * REST controller for managing {@link com.codingaxis.mockwin.domain.CompetExam}.
 */
@Path("/api/compet-exams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CompetExamResource {

    private final Logger log = LoggerFactory.getLogger(CompetExamResource.class);

    private static final String ENTITY_NAME = "mockwinfinalCompetExam";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /compet-exams} : Create a new competExam.
     *
     * @param competExam the competExam to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new competExam, or with status {@code 400 (Bad Request)} if the competExam has already an ID.
     */
    @POST
    @Transactional
    public Response createCompetExam(@Valid CompetExam competExam, @Context UriInfo uriInfo) {
        log.debug("REST request to save CompetExam : {}", competExam);
        if (competExam.id != null) {
            throw new BadRequestAlertException("A new competExam cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = CompetExam.persistOrUpdate(competExam);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /compet-exams} : Updates an existing competExam.
     *
     * @param competExam the competExam to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated competExam,
     * or with status {@code 400 (Bad Request)} if the competExam is not valid,
     * or with status {@code 500 (Internal Server Error)} if the competExam couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateCompetExam(@Valid CompetExam competExam) {
        log.debug("REST request to update CompetExam : {}", competExam);
        if (competExam.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = CompetExam.persistOrUpdate(competExam);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, competExam.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /compet-exams/:id} : delete the "id" competExam.
     *
     * @param id the id of the competExam to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteCompetExam(@PathParam("id") Long id) {
        log.debug("REST request to delete CompetExam : {}", id);
        CompetExam.findByIdOptional(id).ifPresent(competExam -> {
            competExam.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /compet-exams} : get all the competExams.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of competExams in body.
     */
    @GET
    public List<CompetExam> getAllCompetExams() {
        log.debug("REST request to get all CompetExams");
        return CompetExam.findAll().list();
    }


    /**
     * {@code GET  /compet-exams/:id} : get the "id" competExam.
     *
     * @param id the id of the competExam to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the competExam, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getCompetExam(@PathParam("id") Long id) {
        log.debug("REST request to get CompetExam : {}", id);
        Optional<CompetExam> competExam = CompetExam.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(competExam);
    }
}

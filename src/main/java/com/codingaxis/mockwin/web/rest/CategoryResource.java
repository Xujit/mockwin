package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codingaxis.mockwin.domain.Category;
import com.codingaxis.mockwin.web.rest.errors.BadRequestAlertException;
import com.codingaxis.mockwin.web.util.HeaderUtil;
import com.codingaxis.mockwin.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.codingaxis.mockwin.domain.Category}.
 */
@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CategoryResource {

  private final Logger log = LoggerFactory.getLogger(CategoryResource.class);

  private static final String ENTITY_NAME = "mockwinfinalCategory";

  @ConfigProperty(name = "application.name")
  String applicationName;

  /**
   * {@code POST  /categories} : Create a new category.
   *
   * @param category the category to create.
   * @return the {@link Response} with status {@code 201 (Created)} and with body the new category, or with status
   *         {@code 400 (Bad Request)} if the category has already an ID.
   */
  @POST
  @Transactional
  public Response createCategory(@Valid Category category, @Context UriInfo uriInfo) {

    this.log.debug("REST request to save Category : {}", category);
    if (category.id != null) {
      throw new BadRequestAlertException("A new category cannot already have an ID", ENTITY_NAME, "idexists");
    }
    var result = Category.persistOrUpdate(category);
    var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
    HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code PUT  /categories} : Updates an existing category.
   *
   * @param category the category to update.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the updated category, or with status
   *         {@code 400 (Bad Request)} if the category is not valid, or with status {@code 500 (Internal Server Error)}
   *         if the category couldn't be updated.
   */
  @PUT
  @Transactional
  public Response updateCategory(@Valid Category category) {

    this.log.debug("REST request to update Category : {}", category);
    if (category.id == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    var result = Category.persistOrUpdate(category);
    var response = Response.ok().entity(result);
    HeaderUtil.createEntityUpdateAlert(this.applicationName, true, ENTITY_NAME, category.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code DELETE  /categories/:id} : delete the "id" category.
   *
   * @param id the id of the category to delete.
   * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteCategory(@PathParam("id") Long id) {

    this.log.debug("REST request to delete Category : {}", id);
    Category.findByIdOptional(id).ifPresent(category -> {
      category.delete();
    });
    var response = Response.noContent();
    HeaderUtil.createEntityDeletionAlert(this.applicationName, true, ENTITY_NAME, id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code GET  /categories} : get all the categories. * @return the {@link Response} with status {@code 200 (OK)} and
   * the list of categories in body.
   */
  @GET
  public List<Category> getAllCategories() {

    this.log.debug("REST request to get all Categories");
    return Category.findAll().list();
  }

  /**
   * {@code GET  /categories/:id} : get the "id" category.
   *
   * @param id the id of the category to retrieve.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the category, or with status
   *         {@code 404 (Not Found)}.
   */
  @GET
  @Path("/{id}")

  public Response getCategory(@PathParam("id") Long id) {

    this.log.debug("REST request to get Category : {}", id);
    Optional<Category> category = Category.findByIdOptional(id);
    return ResponseUtil.wrapOrNotFound(category);
  }
}

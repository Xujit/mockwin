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

import com.codingaxis.mockwin.domain.Country;
import com.codingaxis.mockwin.web.rest.errors.BadRequestAlertException;
import com.codingaxis.mockwin.web.util.HeaderUtil;
import com.codingaxis.mockwin.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.codingaxis.mockwin.domain.Country}.
 */
@Path("/api/countries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CountryResource {

  private final Logger log = LoggerFactory.getLogger(CountryResource.class);

  private static final String ENTITY_NAME = "mockwinfinalCountry";

  @ConfigProperty(name = "application.name")
  String applicationName;

  /**
   * {@code POST  /countries} : Create a new country.
   *
   * @param country the country to create.
   * @return the {@link Response} with status {@code 201 (Created)} and with body the new country, or with status
   *         {@code 400 (Bad Request)} if the country has already an ID.
   */
  @POST
  @Transactional
  public Response createCountry(@Valid Country country, @Context UriInfo uriInfo) {

    this.log.debug("REST request to save Country : {}", country);
    if (country.id != null) {
      throw new BadRequestAlertException("A new country cannot already have an ID", ENTITY_NAME, "idexists");
    }
    var result = Country.persistOrUpdate(country);
    var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
    HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code PUT  /countries} : Updates an existing country.
   *
   * @param country the country to update.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the updated country, or with status
   *         {@code 400 (Bad Request)} if the country is not valid, or with status {@code 500 (Internal Server Error)}
   *         if the country couldn't be updated.
   */
  @PUT
  @Transactional
  public Response updateCountry(@Valid Country country) {

    this.log.debug("REST request to update Country : {}", country);
    if (country.id == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    var result = Country.persistOrUpdate(country);
    var response = Response.ok().entity(result);
    HeaderUtil.createEntityUpdateAlert(this.applicationName, true, ENTITY_NAME, country.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code DELETE  /countries/:id} : delete the "id" country.
   *
   * @param id the id of the country to delete.
   * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteCountry(@PathParam("id") Long id) {

    this.log.debug("REST request to delete Country : {}", id);
    Country.findByIdOptional(id).ifPresent(country -> {
      country.delete();
    });
    var response = Response.noContent();
    HeaderUtil.createEntityDeletionAlert(this.applicationName, true, ENTITY_NAME, id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code GET  /countries} : get all the countries. * @return the {@link Response} with status {@code 200 (OK)} and
   * the list of countries in body.
   */
  @GET
  public List<Country> getAllCountries() {

    this.log.debug("REST request to get all Countries");
    return Country.findAll().list();
  }

  /**
   * {@code GET  /countries/:id} : get the "id" country.
   *
   * @param id the id of the country to retrieve.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the country, or with status
   *         {@code 404 (Not Found)}.
   */
  @GET
  @Path("/{id}")

  public Response getCountry(@PathParam("id") Long id) {

    this.log.debug("REST request to get Country : {}", id);
    Optional<Country> country = Country.findByIdOptional(id);
    return ResponseUtil.wrapOrNotFound(country);
  }
}

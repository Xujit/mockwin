package com.codingaxis.mockwin.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codingaxis.mockwin.domain.BannerContest;
import com.codingaxis.mockwin.domain.Contest;
import com.codingaxis.mockwin.web.rest.errors.BadRequestAlertException;
import com.codingaxis.mockwin.web.util.HeaderUtil;
import com.codingaxis.mockwin.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.codingaxis.mockwin.domain.BannerContest}.
 */
@Path("/api/banner-contests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class BannerContestResource {

  private final Logger log = LoggerFactory.getLogger(BannerContestResource.class);

  private static final String ENTITY_NAME = "mockwinfinalBannerContest";

  public final static String SEND_FOR_APPROVAL = "SEND_FOR_APPROVAL";

  public static final String REJECTED = "REJECTED";

  private static final String BANNER_APPROVED = "APPROVED";

  @ConfigProperty(name = "application.name")
  String applicationName;

  @Path("/user/{userid}")
  public Map<String, List<BannerContest>> getBannersByStatus(@PathParam("userId") Long userId) {

    List<BannerContest> bannerContests = BannerContest.findByAssignedTo(userId);
    Map<String, List<BannerContest>> bannerGroup = null;
    if (bannerContests.size() > 0) {
      bannerGroup = bannerContests.stream().collect(Collectors.groupingBy(BannerContest::getStatus));
    }
    return bannerGroup;
  }

  @Path("/approve/user/{userId}/contest/{contestId}/file/{fileName}")
  public String approveBanner(@PathParam("userId") Long userId, //
      @PathParam("contestId") Long contestId, //
      @PathParam("fileName") String fileName) {

    BannerContest approvedbanner = BannerContest.findByContestIdAndStatus(contestId, BANNER_APPROVED);

    if (approvedbanner != null) {
      BannerContest bannerTobeApproved = BannerContest.findByAssignedTo(userId).stream()
          .filter(b -> b.id.equals(contestId) && b.getStatus().equalsIgnoreCase(SEND_FOR_APPROVAL)).findFirst().get();
      bannerTobeApproved.approvedFile = fileName;
      bannerTobeApproved.status = BANNER_APPROVED;
      BannerContest.persistOrUpdate(bannerTobeApproved);

      Contest contestTobeUpdated = Contest.findById(contestId);
      // contestTobeUpdated.fileName = fileName;
      contestTobeUpdated.fileId = 0L; // TODO getfile Id and save
      Contest.persistOrUpdate(contestTobeUpdated);
      return "success";
    }

    return "failed";
  }

  @Path("/sendForAppoval/user/{userId}")
  @POST
  public BannerContest sendforApproval(@PathParam("userId") Long userId, @RequestBody BannerContest bannerContest) {

    List<BannerContest> exisBannerContests = BannerContest.findByAssignedTo(userId);
    if (exisBannerContests.size() > 0) {

      BannerContest exisitngBanner = exisBannerContests.stream() //
          .filter(b -> b.id.equals(bannerContest.id)).findFirst().get();
      exisitngBanner.status = SEND_FOR_APPROVAL;
      return BannerContest.persistOrUpdate(exisitngBanner);
    }
    return null;
  }

  @Path("/decline/user/{userId}")
  @POST
  public BannerContest declineBanner(@PathParam("userId") Long userId, @RequestBody BannerContest bannerContest) {

    List<BannerContest> bannerContests = BannerContest.findByAssignedToAndContestId(userId, bannerContest.id);
    if (bannerContests.size() > 0) {
      BannerContest existingbannerContest = bannerContests.stream()
          .filter(b -> b.getStatus().equalsIgnoreCase(SEND_FOR_APPROVAL)).findFirst().get();
      existingbannerContest.status = REJECTED;
      existingbannerContest.reason = existingbannerContest.reason;
      return BannerContest.persistOrUpdate(existingbannerContest);
    }
    return null;
  }

  /**
   * {@code POST  /banner-contests} : Create a new bannerContest.
   *
   * @param bannerContest the bannerContest to create.
   * @return the {@link Response} with status {@code 201 (Created)} and with body the new bannerContest, or with status
   *         {@code 400 (Bad Request)} if the bannerContest has already an ID.
   */
  @POST
  @Transactional
  public Response createBannerContest(@Valid BannerContest bannerContest, @Context UriInfo uriInfo) {

    this.log.debug("REST request to save BannerContest : {}", bannerContest);
    if (bannerContest.id != null) {
      throw new BadRequestAlertException("A new bannerContest cannot already have an ID", ENTITY_NAME, "idexists");
    }
    var result = BannerContest.persistOrUpdate(bannerContest);
    var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
    HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code PUT  /banner-contests} : Updates an existing bannerContest.
   *
   * @param bannerContest the bannerContest to update.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the updated bannerContest, or with status
   *         {@code 400 (Bad Request)} if the bannerContest is not valid, or with status
   *         {@code 500 (Internal Server Error)} if the bannerContest couldn't be updated.
   */
  @PUT
  @Transactional
  public Response updateBannerContest(@Valid BannerContest bannerContest) {

    this.log.debug("REST request to update BannerContest : {}", bannerContest);
    if (bannerContest.id == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    var result = BannerContest.persistOrUpdate(bannerContest);
    var response = Response.ok().entity(result);
    HeaderUtil.createEntityUpdateAlert(this.applicationName, true, ENTITY_NAME, bannerContest.id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code DELETE  /banner-contests/:id} : delete the "id" bannerContest.
   *
   * @param id the id of the bannerContest to delete.
   * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteBannerContest(@PathParam("id") Long id) {

    this.log.debug("REST request to delete BannerContest : {}", id);
    BannerContest.findByIdOptional(id).ifPresent(bannerContest -> {
      bannerContest.delete();
    });
    var response = Response.noContent();
    HeaderUtil.createEntityDeletionAlert(this.applicationName, true, ENTITY_NAME, id.toString())
        .forEach(response::header);
    return response.build();
  }

  /**
   * {@code GET  /banner-contests} : get all the bannerContests. * @return the {@link Response} with status
   * {@code 200 (OK)} and the list of bannerContests in body.
   */
  @GET
  public List<BannerContest> getAllBannerContests() {

    this.log.debug("REST request to get all BannerContests");
    return BannerContest.findAll().list();
  }

  /**
   * {@code GET  /banner-contests/:id} : get the "id" bannerContest.
   *
   * @param id the id of the bannerContest to retrieve.
   * @return the {@link Response} with status {@code 200 (OK)} and with body the bannerContest, or with status
   *         {@code 404 (Not Found)}.
   */
  @GET
  @Path("/{id}")

  public Response getBannerContest(@PathParam("id") Long id) {

    this.log.debug("REST request to get BannerContest : {}", id);
    Optional<BannerContest> bannerContest = BannerContest.findByIdOptional(id);
    return ResponseUtil.wrapOrNotFound(bannerContest);
  }
}

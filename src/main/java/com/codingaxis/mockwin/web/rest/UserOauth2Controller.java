package com.codingaxis.mockwin.web.rest;

import io.quarkus.security.Authenticated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Controller to authenticate users.
 */
@Path("/")
@RequestScoped
public class UserOauth2Controller {
    private final Logger log = LoggerFactory.getLogger(UserOauth2Controller.class);

    @GET
    @Path("oauth2/authorization/oidc")
    @Authenticated
    public void initCodeFlow() {
        //Never executed
        return;
    }

    @GET
    @Path("login/oauth2/code/oidc")
    @Authenticated
    public Response callback(@Context UriInfo uriInfo) {
        return Response.status(Response.Status.FOUND).location(uriInfo.getBaseUri()).build();
    }
}

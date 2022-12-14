package com.codingaxis.mockwin.web.rest;

import com.codingaxis.mockwin.config.JHipsterProperties;
import io.quarkus.oidc.IdToken;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * REST controller for managing global OIDC logout.
 */
@Path("/api/logout")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class LogoutResource {

    @Inject
    JHipsterProperties jHipsterProperties;

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Context
    HttpServletRequest request;

    @Context
    HttpServletResponse response;

    @ConfigProperty(name = "quarkus.oidc.authentication.cookie-path")
    String cookiePath;

    /**
     * {@code POST  /api/logout} : logout the current user.
     *
     * @return the {@link Response} with status {@code 200 (OK)} and a body with a global logout URL and ID token.
     */
    @POST
    @Authenticated
    public Response logout() {
        request.getSession().invalidate();
        var cookies = request.getCookies();
        if (cookies != null)
            asList(cookies).forEach(cookie -> {
                cookie.setValue("");
                cookie.setPath(cookiePath);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            });
        var logoutDetails = Map.of(
            "logoutUrl", jHipsterProperties.oidc.logoutUrl,
            "idToken", idToken.getRawToken()
        );
        return Response.ok(logoutDetails).build();
    }
}

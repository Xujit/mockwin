package com.codingaxis.mockwin.web.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Resource to return information about OIDC properties
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class AuthInfoResource {

    final String issuer;
    final String clientId;

    @Inject
    public AuthInfoResource(
        @ConfigProperty(name = "quarkus.oidc.auth-server-url") String issuer,
        @ConfigProperty(name = "quarkus.oidc.client-id") String clientId) {
        this.issuer = issuer;
        this.clientId = clientId;
    }

    @GET
    @Path("/auth-info")
    @PermitAll
    public AuthInfoVM getAuthInfo() {
        return new AuthInfoVM(issuer, clientId);
    }

    @RegisterForReflection
    public static class AuthInfoVM {
        public final String issuer;
        public final String clientId;

        AuthInfoVM(String issuer, String clientId) {
            this.issuer = issuer;
            this.clientId = clientId;
        }
    }
}

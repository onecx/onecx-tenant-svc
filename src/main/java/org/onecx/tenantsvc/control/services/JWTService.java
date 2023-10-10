package org.onecx.tenantsvc.control.services;

import static java.lang.String.format;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.onecx.tenantsvc.domain.exceptions.CouldNotReadFieldOfTokenException;

import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class JWTService {

    @Context
    UriInfo uriInfo;
    @Inject
    JsonWebToken jsonWebToken;

    @ConfigProperty(name = "onecx.tenant-svc.orgIdTokenField")
    String orgIdField;

    public String readOrgIdFromAuthToken() throws CouldNotReadFieldOfTokenException {

        return readOrgIdFromToken(jsonWebToken);
    }

    private String readOrgIdFromToken(JsonWebToken token) throws CouldNotReadFieldOfTokenException {

        if (token == null) {
            throw new CouldNotReadFieldOfTokenException("Token is null");
        }

        var realmAccess = token.getClaim(orgIdField);

        if (realmAccess != null) {
            return realmAccess.toString();
        } else {
            var jwtLoad = extractPayLoadOfToken(token);
            log.error(format("Could not read field: %s of token: %s", orgIdField, jwtLoad));
            throw new CouldNotReadFieldOfTokenException(format("Could not read org ID of field: %s ", orgIdField));
        }
    }

    private static String extractPayLoadOfToken(JsonWebToken token) {

        if (token.getRawToken() == null) {
            return "Token is null";
        }

        var jwtSplit = token.getRawToken().split("\\.");
        var jwtLoad = "";
        if (jwtSplit.length == 3) {
            jwtLoad = jwtSplit[1];
        }
        return jwtLoad;
    }
}

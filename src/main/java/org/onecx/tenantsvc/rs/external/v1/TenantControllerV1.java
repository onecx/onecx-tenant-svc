package org.onecx.tenantsvc.rs.external.v1;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static java.lang.String.format;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.onecx.tenantsvc.domain.daos.TenantMapDAO;

import gen.io.github.onecx.tenantsvc.v1.TenantV1Api;
import gen.io.github.onecx.tenantsvc.v1.model.TenantMapDTOV1;
import io.smallrye.jwt.auth.principal.JWTParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class TenantControllerV1 implements TenantV1Api {

    @Inject
    TenantMapDAO tenantMapDAO;

    @ConfigProperty(name = "onecx.tenant-svc.token.claim.org-id")
    String orgClaim;

    @ConfigProperty(name = "onecx.tenant-svc.header.token")
    String headerParam;

    @Context
    HttpHeaders headers;

    @Inject
    JWTParser parser;

    @Override
    public Response getTenantMapsByOrgId() {

        // read token from header
        var apmPrincipalToken = headers.getHeaderString(headerParam);
        if (apmPrincipalToken == null || apmPrincipalToken.isBlank()) {
            return Response.status(BAD_REQUEST).entity("Missing APM principal token: " + headerParam).build();
        }

        // parse and verify token
        JsonWebToken token;
        try {
            token = parser.parse(apmPrincipalToken);
        } catch (Exception e) {
            return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
        }

        // read claim
        var orgIdClaim = token.claim(orgClaim);
        if (orgIdClaim.isEmpty()) {
            log.error(format("Could not find organization field '%s' in the ID token", orgClaim));
            return Response.status(BAD_REQUEST).entity(format("Could not read org ID of field: %s ", orgClaim)).build();
        }
        var organizationId = orgIdClaim.get().toString();

        // find tenant ID for the organizationId
        var tenantId = tenantMapDAO.findTenantIdByOrgId(organizationId);
        if (tenantId.isEmpty()) {
            return Response.status(NOT_FOUND).entity(format("Did not find tenant map for org ID: %s", organizationId)).build();
        }

        var tenantMapDTO = new TenantMapDTOV1();
        tenantMapDTO.setTenantId(tenantId.get());

        return Response.ok(tenantMapDTO).build();
    }

}

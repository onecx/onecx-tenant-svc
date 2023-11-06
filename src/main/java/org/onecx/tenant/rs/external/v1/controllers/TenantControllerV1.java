package org.onecx.tenant.rs.external.v1.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static java.lang.String.format;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwx.JsonWebStructure;
import org.onecx.tenant.domain.daos.TenantMapDAO;
import org.onecx.tenant.rs.external.v1.mappers.TenantMapperV1;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.io.github.onecx.tenant.v1.TenantV1Api;
import gen.io.github.onecx.tenant.v1.model.RestExceptionDTOV1;
import gen.io.github.onecx.tenant.v1.model.TenantMapDTOV1;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
@Path("/v1/tenant")
public class TenantControllerV1 implements TenantV1Api {

    @Inject
    TenantMapDAO dao;

    @ConfigProperty(name = "onecx.tenant.token.claim.org-id")
    String orgClaim;

    @ConfigProperty(name = "onecx.tenant.token.verified")
    boolean verified;

    @ConfigProperty(name = "onecx.tenant.token.issuer.public-key-location.suffix", defaultValue = "/protocol/openid-connect/certs")
    String issuerPublicKeyLocationSuffix;

    @ConfigProperty(name = "onecx.tenant.token.issuer.public-key-location.enabled")
    boolean issuerEnabled;

    @ConfigProperty(name = "onecx.tenant.header.token")
    String headerParam;

    @Context
    HttpHeaders headers;

    @Inject
    JWTParser parser;

    @Inject
    JWTAuthContextInfo authContextInfo;

    @Inject
    TenantMapperV1 mapper;

    @Override
    public Response getTenantMapsByOrgId() {

        // read token from header
        var apmPrincipalToken = headers.getHeaderString(headerParam);
        if (apmPrincipalToken == null || apmPrincipalToken.isBlank()) {
            return Response.status(BAD_REQUEST).entity("Missing APM principal token: " + headerParam).build();
        }

        // parse and verify? token
        Optional<String> organizationClaim;
        try {
            if (verified) {
                var info = authContextInfo;

                // get public key location from issuer URL
                if (issuerEnabled) {
                    var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(apmPrincipalToken);
                    var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());
                    var publicKeyLocation = jwtClaims.getIssuer() + issuerPublicKeyLocationSuffix;
                    info = new JWTAuthContextInfo(authContextInfo);
                    info.setPublicKeyLocation(publicKeyLocation);
                }

                var token = parser.parse(apmPrincipalToken, info);
                organizationClaim = token.claim(orgClaim);
            } else {
                var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(apmPrincipalToken);
                var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());
                organizationClaim = Optional.of(jwtClaims.getClaimValueAsString(orgClaim));
            }
        } catch (Exception e) {
            log.error("Failed to verify a token. Error: {}", e.getMessage());
            return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
        }

        // read claim
        if (organizationClaim.isEmpty()) {
            log.error("Could not find organization field '{}' in the ID token", orgClaim);
            return Response.status(BAD_REQUEST).entity(format("Could not read org ID of field: %s ", orgClaim)).build();
        }
        var organizationId = organizationClaim.get();

        // find tenant ID for the organizationId
        var tenantId = dao.findTenantIdByOrgId(organizationId);
        if (tenantId.isEmpty()) {
            return Response.status(NOT_FOUND).entity(format("Did not find tenant map for org ID: %s", organizationId)).build();
        }

        var tenantMapDTO = new TenantMapDTOV1();
        tenantMapDTO.setTenantId(tenantId.get());

        return Response.ok(tenantMapDTO).build();
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTOV1> exception(Exception ex) {
        log.error("Process external rest-controller v1 error:{}", ex.getMessage());
        if (ex instanceof DAOException de) {
            return RestResponse.status(Response.Status.BAD_REQUEST,
                    mapper.exception(de.getMessageKey().name(), ex.getMessage(), de.parameters));
        }
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR,
                mapper.exception("UNDEFINED_ERROR_CODE", ex.getMessage()));

    }
}

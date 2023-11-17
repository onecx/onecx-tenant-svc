package org.onecx.tenant.rs.external.v1.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.lang.JoseException;
import org.onecx.tenant.domain.daos.TenantDAO;
import org.onecx.tenant.rs.external.v1.mappers.TenantMapperV1;

import gen.io.github.onecx.tenant.v1.TenantV1Api;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
@Path("/v1/tenant")
public class TenantControllerV1 implements TenantV1Api {

    @Inject
    TenantDAO dao;

    @ConfigProperty(name = "onecx.tenant.token.claim.org-id")
    String orgClaim;

    @ConfigProperty(name = "onecx.tenant.token.verified")
    boolean verified;

    @ConfigProperty(name = "onecx.tenant.token.issuer.public-key-location.suffix")
    String issuerPublicKeyLocationSuffix;

    @ConfigProperty(name = "onecx.tenant.token.issuer.public-key-location.enabled")
    boolean issuerEnabled;

    @ConfigProperty(name = "onecx.tenant.header.token")
    String headerParam;

    @ConfigProperty(name = "onecx.tenant.default.enabled")
    boolean defaultTenantEnabled;

    @ConfigProperty(name = "onecx.tenant.default.tenant-id")
    String defaultTenantValue;

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
            log.error("Missing APM principal token: " + headerParam);
            return Response.status(BAD_REQUEST)
                    .entity(mapper.exception(ErrorKeys.ERROR_MISSING_APM_PRINCIPAL_TOKEN,
                            "Missing APM principal token: " + headerParam))
                    .build();
        }

        // read organization from token
        String organizationId;
        try {
            organizationId = getOrganization(apmPrincipalToken);
        } catch (Exception e) {
            log.error("Failed to verify a token. Error: {}", e.getMessage());
            return Response.status(BAD_REQUEST)
                    .entity(mapper.exception(ErrorKeys.ERROR_VERIFY_APM_TOKEN,
                            "Failed to verify a token. Error: " + e.getMessage()))
                    .build();
        }

        // validate organization
        if (organizationId == null) {
            if (defaultTenantEnabled) {
                return Response.ok(mapper.create(defaultTenantValue)).build();
            }
            log.error("Could not find organization field '{}' in the ID token", orgClaim);
            return Response.status(BAD_REQUEST)
                    .entity(mapper.exception(ErrorKeys.ERROR_NO_ORGANIZATION_ID_IN_TOKEN,
                            "Could not find organization field '" + orgClaim + "' in the ID token"))
                    .build();
        }

        // find tenant ID for the organizationId
        var tenantId = dao.findTenantIdByOrgId(organizationId);
        if (tenantId.isPresent()) {
            return Response.ok(mapper.create(tenantId.get())).build();
        }

        // check if default tenant is enabled
        if (defaultTenantEnabled) {
            return Response.ok(mapper.create(defaultTenantValue)).build();
        }

        return Response.status(NOT_FOUND).build();
    }

    private String getOrganization(String apmPrincipalToken)
            throws JoseException, InvalidJwtException, MalformedClaimException, ParseException {
        Optional<String> organizationClaim;

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

        return organizationClaim.orElse(null);

    }

    public enum ErrorKeys {

        ERROR_NO_ORGANIZATION_ID_IN_TOKEN,

        ERROR_MISSING_APM_PRINCIPAL_TOKEN,
        ERROR_VERIFY_APM_TOKEN;
    }
}

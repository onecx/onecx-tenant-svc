package org.onecx.tenant.rs.external.v1.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.lang.JoseException;
import org.onecx.tenant.domain.daos.TenantDAO;
import org.onecx.tenant.rs.external.TenantConfig;
import org.onecx.tenant.rs.external.v1.mappers.TenantMapperV1;

import gen.io.github.onecx.tenant.v1.TenantV1Api;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class TenantControllerV1 implements TenantV1Api {

    @Inject
    TenantDAO dao;

    @Inject
    TenantConfig config;

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
        var apmPrincipalToken = headers.getHeaderString(config.headerToken());
        if (apmPrincipalToken == null || apmPrincipalToken.isBlank()) {
            log.error("Missing APM principal token: " + config.headerToken());
            return Response.status(BAD_REQUEST)
                    .entity(mapper.exception(ErrorKeys.ERROR_MISSING_APM_PRINCIPAL_TOKEN,
                            "Missing APM principal token: " + config.headerToken()))
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
            if (config.defaultTenantEnabled()) {
                return Response.ok(mapper.create(config.defaultTenantId())).build();
            }
            log.error("Could not find organization field '{}' in the ID token", config.tokenOrgClaim());
            return Response.status(BAD_REQUEST)
                    .entity(mapper.exception(ErrorKeys.ERROR_NO_ORGANIZATION_ID_IN_TOKEN,
                            "Could not find organization field '" + config.tokenOrgClaim() + "' in the ID token"))
                    .build();
        }

        // find tenant ID for the organizationId
        var tenantId = dao.findTenantIdByOrgId(organizationId);
        if (tenantId.isPresent()) {
            return Response.ok(mapper.create(tenantId.get())).build();
        }

        // check if default tenant is enabled
        if (config.defaultTenantEnabled()) {
            return Response.ok(mapper.create(config.defaultTenantId())).build();
        }

        return Response.status(NOT_FOUND).build();
    }

    private String getOrganization(String apmPrincipalToken)
            throws JoseException, InvalidJwtException, MalformedClaimException, ParseException {
        Optional<String> organizationClaim;

        if (config.tokenVerified()) {
            var info = authContextInfo;

            // get public key location from issuer URL
            if (config.tokenPublicKeyEnabled()) {
                var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(apmPrincipalToken);
                var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());
                var publicKeyLocation = jwtClaims.getIssuer() + config.tokenPublicKeyLocationSuffix();
                info = new JWTAuthContextInfo(authContextInfo);
                info.setPublicKeyLocation(publicKeyLocation);
            }

            var token = parser.parse(apmPrincipalToken, info);
            organizationClaim = token.claim(config.tokenOrgClaim());
        } else {
            var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(apmPrincipalToken);
            var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());
            organizationClaim = Optional.of(jwtClaims.getClaimValueAsString(config.tokenOrgClaim()));
        }

        return organizationClaim.orElse(null);

    }

    public enum ErrorKeys {

        ERROR_NO_ORGANIZATION_ID_IN_TOKEN,

        ERROR_MISSING_APM_PRINCIPAL_TOKEN,
        ERROR_VERIFY_APM_TOKEN;
    }
}

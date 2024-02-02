package org.tkit.onecx.tenant.rs.external.v1.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.tenant.domain.daos.TenantDAO;
import org.tkit.onecx.tenant.rs.external.TenantConfig;
import org.tkit.onecx.tenant.rs.external.v1.mappers.TenantMapperV1;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.rs.context.token.TokenException;

import gen.org.tkit.onecx.tenant.v1.TenantV1Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class TenantControllerV1 implements TenantV1Api {

    @Inject
    TenantDAO dao;

    @Inject
    TenantConfig config;

    @Inject
    TenantMapperV1 mapper;

    @Override
    public Response getTenantMapsByOrgId() {

        var context = ApplicationContext.get();
        var principalToken = context.getPrincipalToken();

        // check principal token
        if (principalToken == null) {
            log.error("Missing principal token.");
            return Response.status(BAD_REQUEST)
                    .entity(mapper.exception(ErrorKeys.ERROR_MISSING_PRINCIPAL_TOKEN,
                            "Missing APM principal token."))
                    .build();
        }

        // read organization from token
        Optional<String> organizationClaim = principalToken.claim(config.tokenOrgClaim());
        var organizationId = organizationClaim.orElse(null);

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

    @ServerExceptionMapper
    public Response tokenException(TokenException e) {
        return Response.status(BAD_REQUEST)
                .entity(mapper.exception(e.getKey(), e.getMessage()))
                .build();
    }

    public enum ErrorKeys {

        ERROR_NO_ORGANIZATION_ID_IN_TOKEN,

        ERROR_MISSING_PRINCIPAL_TOKEN;
    }
}

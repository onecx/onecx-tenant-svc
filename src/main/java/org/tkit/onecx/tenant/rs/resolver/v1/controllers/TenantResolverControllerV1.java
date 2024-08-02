package org.tkit.onecx.tenant.rs.resolver.v1.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.tkit.onecx.tenant.domain.daos.TenantDAO;
import org.tkit.onecx.tenant.domain.services.TenantConfig;
import org.tkit.onecx.tenant.rs.resolver.v1.mappers.TenantResolverMapperV1;

import gen.org.tkit.onecx.tenant.resolver.v1.TenantResolverV1Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class TenantResolverControllerV1 implements TenantResolverV1Api {

    @Inject
    TenantDAO dao;

    @Inject
    TenantConfig config;

    @Inject
    TenantResolverMapperV1 mapper;

    @Override
    public Response getTenantByOrgId(String orgId) {

        // find tenant ID for the organizationId
        var tenantId = dao.findTenantIdByOrgId(orgId);
        if (tenantId.isPresent()) {
            return Response.ok(mapper.create(tenantId.get())).build();
        }

        // check if default tenant is enabled
        if (config.resolver().defaultTenantEnabled()) {
            return Response.ok(mapper.create(config.resolver().defaultTenantId())).build();
        }

        return Response.status(NOT_FOUND).build();
    }

}

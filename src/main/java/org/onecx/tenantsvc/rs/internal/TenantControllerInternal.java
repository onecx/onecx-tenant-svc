package org.onecx.tenantsvc.rs.internal;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.onecx.tenantsvc.domain.daos.TenantMapDAO;

import gen.io.github.onecx.tenantsvc.rs.internal.TenantInternalApi;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class TenantControllerInternal implements TenantInternalApi {

    @Inject
    TenantMapDAO tenantMapDAO;

    @Override
    public Response getAllTenants() {

        return Response.ok(tenantMapDAO.findAll().toList()).build();
    }

    // TODO: add POST endpoint
    // TODO: add PUT endpoint

    @Override
    public Response deleteTenantbyId(String id) {

        tenantMapDAO.deleteQueryById(id);

        return Response.noContent().build();
    }
}

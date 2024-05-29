package org.tkit.onecx.tenant.rs.exim.v1.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.tenant.domain.daos.TenantDAO;
import org.tkit.onecx.tenant.domain.models.Tenant;
import org.tkit.onecx.tenant.rs.exim.v1.mappers.EximExceptionMapperV1;
import org.tkit.onecx.tenant.rs.exim.v1.mappers.EximMapperV1;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.tenant.exim.v1.TenantExportImportApi;
import gen.org.tkit.onecx.tenant.exim.v1.model.EximProblemDetailResponseDTOV1;
import gen.org.tkit.onecx.tenant.exim.v1.model.TenantsSnapshotDTOV1;

@LogService
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class TenantEximV1Controller implements TenantExportImportApi {

    @Inject
    EximMapperV1 mapper;

    @Inject
    EximExceptionMapperV1 exceptionMapper;

    @Inject
    TenantDAO dao;

    @Override
    public Response operatorImportTenants(TenantsSnapshotDTOV1 request) {

        if (request.getTenants() == null || request.getTenants().isEmpty()) {
            return Response.ok().build();
        }

        var existingTenantIds = dao.filterExistingTenants(request.getTenants().keySet());

        List<Tenant> tenants = mapper.createTenants(request, existingTenantIds);
        dao.create(tenants);

        return Response.ok().build();
    }

    @ServerExceptionMapper
    public RestResponse<EximProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}

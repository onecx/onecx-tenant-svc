package org.onecx.tenantsvc.rs.internal;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static java.lang.String.format;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.onecx.tenantsvc.domain.daos.TenantMapDAO;
import org.onecx.tenantsvc.domain.mappers.TenantMapMapper;

import gen.io.github.onecx.tenantsvc.rs.internal.TenantInternalApi;
import gen.io.github.onecx.tenantsvc.rs.internal.model.RequestTenantMapDTO;
import gen.io.github.onecx.tenantsvc.rs.internal.model.ResponseTenantMapDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class TenantControllerInternal implements TenantInternalApi {

    @Inject
    TenantMapDAO tenantMapDAO;
    @Inject
    TenantMapMapper tenantMapMapper;

    @Override
    public Response getAllTenantMaps() {

        var tenantMaps = tenantMapDAO.findAll().toList();
        return Response.ok(tenantMapMapper.map(tenantMaps)).build();
    }

    @Override
    public Response updateTenantMapById(String id, RequestTenantMapDTO requestTenantMapDTO) {

        var tenantMap = tenantMapDAO.findById(id);
        if (tenantMap == null) {
            return Response.status(NOT_FOUND).entity(format("Could not find tenantMap with ID: %s", id)).build();
        }

        tenantMapMapper.update(requestTenantMapDTO.getInputTenantMap(), tenantMap);
        var updatedTenantMap = tenantMapDAO.update(tenantMap);
        var updatedTenantMapDTO = tenantMapMapper.map(updatedTenantMap);
        var responseTenantMapDTO = new ResponseTenantMapDTO();
        responseTenantMapDTO.setTenantMap(updatedTenantMapDTO);

        return Response.ok(responseTenantMapDTO).build();
    }

    @Override
    public Response createTenantMap(RequestTenantMapDTO requestTenantMapDTO) {

        var tenantMap = tenantMapMapper.create(requestTenantMapDTO.getInputTenantMap());
        var responseTenantMapDTO = new ResponseTenantMapDTO();
        responseTenantMapDTO.setTenantMap(tenantMapMapper.map(tenantMap));
        return Response.status(CREATED).entity(responseTenantMapDTO).build();
    }

    @Override
    public Response deleteTenantMapById(String id) {

        tenantMapDAO.deleteQueryById(id);

        return Response.noContent().build();
    }
}

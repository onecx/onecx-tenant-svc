package org.onecx.tenantsvc.rs.external.v1;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static java.lang.String.format;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.onecx.tenantsvc.control.services.JWTService;
import org.onecx.tenantsvc.domain.daos.TenantMapDAO;
import org.onecx.tenantsvc.domain.exceptions.CouldNotReadFieldOfTokenException;

import gen.io.github.onecx.tenantsvc.v1.TenantV1Api;
import gen.io.github.onecx.tenantsvc.v1.model.ResponseTenantMapDTOV1;
import gen.io.github.onecx.tenantsvc.v1.model.TenantMapDTOV1;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class TenantControllerV1 implements TenantV1Api {

    @Inject
    TenantMapDAO tenantMapDAO;
    @Inject
    JWTService jwtService;

    @Override
    public Response getTenantMapsByOrgId() {

        String orgId;
        try {
            orgId = jwtService.readOrgIdFromAuthToken();
        } catch (CouldNotReadFieldOfTokenException e) {
            return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
        }

        var tenantId = tenantMapDAO.findTenantIdByOrgId(orgId);
        if (tenantId.isEmpty()) {
            return Response.status(NOT_FOUND).entity(format("Did not find tenant map for org ID: %s", orgId)).build();
        }

        var tenantResponseDTO = new ResponseTenantMapDTOV1();
        var tenantMapDTO = new TenantMapDTOV1();
        tenantMapDTO.setTenantId(tenantId.get());
        tenantResponseDTO.setTenantMap(tenantMapDTO);

        return Response.ok(tenantResponseDTO).build();
    }
}

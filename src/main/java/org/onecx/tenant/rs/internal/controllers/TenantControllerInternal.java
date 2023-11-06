package org.onecx.tenant.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static java.lang.String.format;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.onecx.tenant.domain.daos.TenantMapDAO;
import org.onecx.tenant.rs.internal.mappers.TenantMapMapper;
import org.tkit.quarkus.jpa.daos.Page;

import gen.io.github.onecx.tenant.rs.internal.TenantInternalApi;
import gen.io.github.onecx.tenant.rs.internal.model.CreateRequestTenantMapDTO;
import gen.io.github.onecx.tenant.rs.internal.model.RequestTenantMapDTO;
import gen.io.github.onecx.tenant.rs.internal.model.RestExceptionDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Path("/internal/tenant")
@Transactional(value = NOT_SUPPORTED)
public class TenantControllerInternal implements TenantInternalApi {

    @Inject
    TenantMapDAO dao;
    @Inject
    TenantMapMapper mapper;

    @Context
    UriInfo uriInfo;

    @Override
    @Transactional
    public Response getAllTenantMaps(Integer pageNumber, Integer pageSize) {

        var tenantMaps = dao.createPageQuery(Page.of(pageNumber, pageSize))
                .getPageResult()
                .getStream()
                .toList();

        var responseTenantMapsDTO = mapper.response(tenantMaps);

        return Response.ok(responseTenantMapsDTO).build();
    }

    @Override
    @Transactional
    public Response updateTenantMapById(String id, RequestTenantMapDTO requestTenantMapDTO) {

        var tenantMap = dao.findById(id);
        if (tenantMap == null) {
            return Response.status(NOT_FOUND).entity(format("Could not find tenantMap with ID: %s", id)).build();
        }

        mapper.update(requestTenantMapDTO.getInputTenantMap(), tenantMap);
        var updatedTenantMap = dao.update(tenantMap);
        var responseTenantMapDTO = mapper.response(updatedTenantMap);

        return Response.ok(responseTenantMapDTO).build();
    }

    @Override
    @Transactional
    public Response createTenantMap(CreateRequestTenantMapDTO requestTenantMapDTO) {

        var tenantMap = mapper.create(requestTenantMapDTO.getInputTenantMap());
        tenantMap = dao.create(tenantMap);

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(tenantMap.getId()).build())
                .build();
    }

    @Override
    public Response getTenantMapById(String id) {
        var tenantMap = dao.findById(id);
        return Response.ok(mapper.map(tenantMap)).build();
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTO> exception(Exception ex) {
        return mapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTO> constraint(ConstraintViolationException ex) {
        return mapper.constraint(ex);
    }
}

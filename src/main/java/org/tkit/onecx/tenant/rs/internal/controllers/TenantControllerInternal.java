package org.tkit.onecx.tenant.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.tenant.domain.daos.TenantDAO;
import org.tkit.onecx.tenant.rs.internal.mappers.TenantMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;

import gen.org.tkit.onecx.tenant.rs.internal.TenantInternalApi;
import gen.org.tkit.onecx.tenant.rs.internal.model.CreateTenantRequestDTO;
import gen.org.tkit.onecx.tenant.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.tenant.rs.internal.model.TenantSearchCriteriaDTO;
import gen.org.tkit.onecx.tenant.rs.internal.model.UpdateTenantRequestDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class TenantControllerInternal implements TenantInternalApi {

    @Inject
    TenantDAO dao;
    @Inject
    TenantMapper mapper;

    @Context
    UriInfo uriInfo;

    @Override
    @Transactional
    public Response updateTenant(String id, UpdateTenantRequestDTO dto) {

        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.update(dto, item);
        item = dao.update(item);
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    @Transactional
    public Response createTenant(CreateTenantRequestDTO dto) {
        var item = mapper.create(dto);
        item = dao.create(item);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(item.getId()).build())
                .entity(mapper.map(item))
                .build();
    }

    @Override
    public Response getTenant(String id) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    public Response searchTenants(TenantSearchCriteriaDTO tenantSearchCriteriaDTO) {
        var criteria = mapper.map(tenantSearchCriteriaDTO);
        var result = dao.findThemesByCriteria(criteria);
        return Response.ok(mapper.mapPage(result)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return mapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return mapper.constraint(ex);
    }
}

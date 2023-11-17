package org.onecx.tenant.rs.internal.mappers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.onecx.tenant.domain.criteria.TenantSearchCriteria;
import org.onecx.tenant.domain.models.Tenant;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.tenant.rs.internal.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class TenantMapper {

    public abstract TenantSearchCriteria map(TenantSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    public abstract TenantPageResultDTO mapPage(PageResult<Tenant> page);

    public abstract List<TenantDTO> map(Stream<Tenant> entity);

    public abstract TenantDTO map(Tenant data);

    public abstract List<TenantDTO> map(List<Tenant> tenantMap);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    public abstract Tenant create(CreateTenantRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    public abstract void update(UpdateTenantRequestDTO tenantMapDTO, @MappingTarget Tenant tenantMap);

    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        var dto = exception("CONSTRAINT_VIOLATIONS", ex.getMessage());
        dto.setInvalidParams(createErrorValidationResponse(ex.getConstraintViolations()));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        var dto = exception(ex.getMessageKey().name(), ex.getConstraints());
        dto.setParams(map(ex.namedParameters));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    @Mapping(target = "removeParamsItem", ignore = true)
    @Mapping(target = "params", ignore = true)
    @Mapping(target = "invalidParams", ignore = true)
    @Mapping(target = "removeInvalidParamsItem", ignore = true)
    public abstract ProblemDetailResponseDTO exception(String errorCode, String detail);

    public List<ProblemDetailParamDTO> map(Map<String, Object> params) {
        if (params == null) {
            return List.of();
        }
        return params.entrySet().stream().map(e -> {
            var item = new ProblemDetailParamDTO();
            item.setKey(e.getKey());
            if (e.getValue() != null) {
                item.setValue(e.getValue().toString());
            }
            return item;
        }).toList();
    }

    public abstract List<ProblemDetailInvalidParamDTO> createErrorValidationResponse(
            Set<ConstraintViolation<?>> constraintViolation);

    @Mapping(target = "name", source = "propertyPath")
    @Mapping(target = "message", source = "message")
    public abstract ProblemDetailInvalidParamDTO createError(ConstraintViolation<?> constraintViolation);

    public String mapPath(Path path) {
        return path.toString();
    }
}

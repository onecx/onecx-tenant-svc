package org.onecx.tenant.rs.internal.mappers;

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.onecx.tenant.domain.models.TenantMap;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.tenant.rs.internal.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class TenantMapMapper {

    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "removeParametersItem", ignore = true)
    @Mapping(target = "validations", ignore = true)
    @Mapping(target = "removeValidationsItem", ignore = true)
    public abstract RestExceptionDTO exception(String errorCode, String message);

    @Mapping(target = "removeParametersItem", ignore = true)
    @Mapping(target = "validations", ignore = true)
    @Mapping(target = "removeValidationsItem", ignore = true)
    public abstract RestExceptionDTO exception(String errorCode, String message, List<Object> parameters);

    public abstract ResponseTenantMapDTO response(TenantMap tenantMap);

    public ResponseTenantMapsDTO response(List<TenantMap> tenantMap) {
        var dto = new ResponseTenantMapsDTO();
        dto.setTenantMaps(map(tenantMap));
        return dto;
    }

    public abstract TenantMapDTO map(TenantMap data);

    public abstract List<TenantMapDTO> map(List<TenantMap> tenantMap);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    public abstract TenantMap create(CreateInputTenantMapDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    public abstract void update(InputTenantMapDTO tenantMapDTO, @MappingTarget TenantMap tenantMap);

    public RestResponse<RestExceptionDTO> constraint(ConstraintViolationException ex) {
        log.error("Processing rest validation error: {}", ex.getMessage());
        var dto = exception("CONSTRAINT_VIOLATIONS", ex.getMessage());
        dto.setValidations(createErrorValidationResponse(ex.getConstraintViolations()));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    public RestResponse<RestExceptionDTO> exception(Exception ex) {
        log.error("Processing portal internal rest controller error: {}", ex.getMessage());

        if (ex instanceof DAOException de) {
            return RestResponse.status(Response.Status.BAD_REQUEST,
                    exception(de.getMessageKey().name(), ex.getMessage(), de.parameters));
        }
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR,
                exception("UNDEFINED_ERROR_CODE", ex.getMessage()));

    }

    public abstract List<ValidationConstraintDTO> createErrorValidationResponse(
            Set<ConstraintViolation<?>> constraintViolation);

    @Mapping(target = "parameter", source = "propertyPath")
    @Mapping(target = "message", source = "message")
    public abstract ValidationConstraintDTO createError(ConstraintViolation<?> constraintViolation);

    public String mapPath(Path path) {
        return path.toString();
    }
}

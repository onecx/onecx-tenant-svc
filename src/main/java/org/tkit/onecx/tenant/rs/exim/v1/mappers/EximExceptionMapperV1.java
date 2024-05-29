package org.tkit.onecx.tenant.rs.exim.v1.mappers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.tenant.exim.v1.model.EximProblemDetailInvalidParamDTOV1;
import gen.org.tkit.onecx.tenant.exim.v1.model.EximProblemDetailParamDTOV1;
import gen.org.tkit.onecx.tenant.exim.v1.model.EximProblemDetailResponseDTOV1;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class EximExceptionMapperV1 {

    public Response importError(List<EximProblemDetailInvalidParamDTOV1> invalidParamDTOV1s) {
        var dto = exception(ErrorCode.INVALID_IMPORT_REQUEST.name(),
                "The request could not be fully completed due to a conflict with the current state of the roles and permissions");
        dto.setInvalidParams(invalidParamDTOV1s);
        return Response.status(Response.Status.CONFLICT).entity(dto).build();
    }

    public RestResponse<EximProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        var dto = exception(ErrorCode.CONSTRAINT_VIOLATIONS.name(), ex.getMessage());
        dto.setInvalidParams(createErrorValidationResponse(ex.getConstraintViolations()));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    @Mapping(target = "removeParamsItem", ignore = true)
    @Mapping(target = "params", ignore = true)
    @Mapping(target = "invalidParams", ignore = true)
    @Mapping(target = "removeInvalidParamsItem", ignore = true)
    public abstract EximProblemDetailResponseDTOV1 exception(String errorCode, String detail);

    public List<EximProblemDetailParamDTOV1> map(Map<String, Object> params) {
        if (params == null) {
            return List.of();
        }
        return params.entrySet().stream().map(e -> {
            var item = new EximProblemDetailParamDTOV1();
            item.setKey(e.getKey());
            if (e.getValue() != null) {
                item.setValue(e.getValue().toString());
            }
            return item;
        }).toList();
    }

    public abstract List<EximProblemDetailInvalidParamDTOV1> createErrorValidationResponse(
            Set<ConstraintViolation<?>> constraintViolation);

    @Mapping(target = "name", source = "propertyPath")
    @Mapping(target = "message", source = "message")
    public abstract EximProblemDetailInvalidParamDTOV1 createError(ConstraintViolation<?> constraintViolation);

    public String mapPath(Path path) {
        return path.toString();
    }

    public enum ErrorCode {

        INVALID_IMPORT_REQUEST,

        CONSTRAINT_VIOLATIONS,
    }
}

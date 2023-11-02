package org.onecx.tenant.rs.external.v1.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.tenant.v1.model.RestExceptionDTOV1;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface TenantMapperV1 {

    RestExceptionDTOV1 exception(String errorCode, String message);

    RestExceptionDTOV1 exception(String errorCode, String message, List<Object> parameters);
}

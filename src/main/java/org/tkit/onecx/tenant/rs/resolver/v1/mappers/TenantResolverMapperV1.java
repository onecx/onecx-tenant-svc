package org.tkit.onecx.tenant.rs.resolver.v1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.tenant.resolver.v1.model.ProblemDetailResponseDTOV1;
import gen.org.tkit.onecx.tenant.resolver.v1.model.TenantIdDTOV1;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface TenantResolverMapperV1 {

    TenantIdDTOV1 create(String tenantId);

    @Mapping(target = "params", ignore = true)
    @Mapping(target = "removeParamsItem", ignore = true)
    @Mapping(target = "invalidParams", ignore = true)
    @Mapping(target = "removeInvalidParamsItem", ignore = true)
    ProblemDetailResponseDTOV1 exception(Enum<?> errorCode, String detail);

    default String map(Enum<?> value) {
        if (value == null) {
            return null;
        }
        return value.name();
    }

}

package org.tkit.onecx.tenant.rs.resolver.v1.mappers;

import org.mapstruct.Mapper;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.tenant.resolver.v1.model.TenantIdDTOV1;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface TenantResolverMapperV1 {

    TenantIdDTOV1 create(String tenantId);

}

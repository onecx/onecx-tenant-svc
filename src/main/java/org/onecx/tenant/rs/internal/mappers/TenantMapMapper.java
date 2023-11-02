package org.onecx.tenant.rs.internal.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.onecx.tenant.domain.models.TenantMap;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.tenant.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface TenantMapMapper {

    RestExceptionDTO exception(String errorCode, String message);

    RestExceptionDTO exception(String errorCode, String message, List<Object> parameters);

    ResponseTenantMapDTO response(TenantMap tenantMap);

    default ResponseTenantMapsDTO response(List<TenantMap> tenantMap) {
        var dto = new ResponseTenantMapsDTO();
        dto.setTenantMaps(map(tenantMap));
        return dto;
    }

    TenantMapDTO map(TenantMap data);

    List<TenantMapDTO> map(List<TenantMap> tenantMap);

    TenantMap create(CreateInputTenantMapDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    void update(InputTenantMapDTO tenantMapDTO, @MappingTarget TenantMap tenantMap);
}

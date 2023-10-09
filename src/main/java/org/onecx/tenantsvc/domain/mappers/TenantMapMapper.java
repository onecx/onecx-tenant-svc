package org.onecx.tenantsvc.domain.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.onecx.tenantsvc.domain.models.TenantMap;

import gen.io.github.onecx.tenantsvc.rs.internal.model.InputTenantMapDTO;
import gen.io.github.onecx.tenantsvc.rs.internal.model.TenantMapDTO;

@Mapper
public abstract class TenantMapMapper {

    public abstract TenantMapDTO map(TenantMap tenantMap);

    public abstract List<TenantMapDTO> map(List<TenantMap> tenantMap);

    public abstract TenantMap create(InputTenantMapDTO dto);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract void update(InputTenantMapDTO tenantMapDTO, @MappingTarget TenantMap tenantMap);
}

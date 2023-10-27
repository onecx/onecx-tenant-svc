package org.onecx.tenant.domain.mappers;

import java.util.List;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.onecx.tenant.domain.models.TenantMap;
import org.onecx.tenant.domain.services.TenantMapService;

import gen.io.github.onecx.tenantsvc.rs.internal.model.InputTenantMapDTO;
import gen.io.github.onecx.tenantsvc.rs.internal.model.TenantMapDTO;

@Mapper
public abstract class TenantMapMapper {

    @Inject
    TenantMapService tenantMapService;

    public abstract TenantMapDTO map(TenantMap tenantMap);

    public abstract List<TenantMapDTO> map(List<TenantMap> tenantMap);

    public TenantMap create(InputTenantMapDTO dto) {

        var tenantMap = new TenantMap();
        tenantMap.setTenantId(tenantMapService.findHighestTenantIdAndAddOne());
        tenantMap.setOrgId(dto.getOrgId());
        return tenantMap;
    }

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
}

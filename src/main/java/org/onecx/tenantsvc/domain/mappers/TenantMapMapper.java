package org.onecx.tenantsvc.domain.mappers;

import java.util.List;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.onecx.tenantsvc.control.services.TimeProvider;
import org.onecx.tenantsvc.domain.models.TenantMap;
import org.onecx.tenantsvc.domain.services.TenantMapService;

import gen.io.github.onecx.tenantsvc.rs.internal.model.InputTenantMapDTO;
import gen.io.github.onecx.tenantsvc.rs.internal.model.TenantMapDTO;

@Mapper
public abstract class TenantMapMapper {

    @Inject
    TenantMapService tenantMapService;
    @Inject
    TimeProvider timeProvider;

    public abstract TenantMapDTO map(TenantMap tenantMap);

    public abstract List<TenantMapDTO> map(List<TenantMap> tenantMap);

    public TenantMap create(InputTenantMapDTO dto) {

        var tenantMap = new TenantMap();
        tenantMap.setTenantId(tenantMapService.findHighestTenantIdAndAddOne());
        tenantMap.setOrgId(dto.getOrgId());
        tenantMap.setCreationDate(timeProvider.localDateTimeNow());
        tenantMap.setModificationCount(0);
        return tenantMap;
    }

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    public void update(InputTenantMapDTO tenantMapDTO, @MappingTarget TenantMap tenantMap) {

        tenantMap.setOrgId(tenantMapDTO.getOrgId());
        tenantMap.setTenantId(tenantMap.getTenantId());
        tenantMap.setModificationDate(timeProvider.localDateTimeNow());
        tenantMap.setModificationCount(tenantMap.getModificationCount() + 1);
    }
}

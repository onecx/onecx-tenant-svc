package org.tkit.onecx.tenant.rs.exim.v1.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.tenant.domain.models.Tenant;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.tenant.exim.v1.model.EximTenantDTOV1;
import gen.org.tkit.onecx.tenant.exim.v1.model.TenantsSnapshotDTOV1;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface EximMapperV1 {

    default List<Tenant> createTenants(TenantsSnapshotDTOV1 request, List<String> existingTenantIds) {
        List<Tenant> tenants = new ArrayList<>();
        request.getTenants().forEach((id, dto) -> {
            if (!existingTenantIds.contains(id) && dto != null) {
                tenants.add(create(id, dto));
            }
        });
        return tenants;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "operator", constant = "true")
    Tenant create(String tenantId, EximTenantDTOV1 dto);
}

package org.tkit.onecx.tenant.rs.exim.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.tenant.exim.v1.model.TenantsSnapshotDTOV1;

@ApplicationScoped
public class ExportImportLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, TenantsSnapshotDTOV1.class,
                        x -> x.getClass().getSimpleName() + ":" + ((TenantsSnapshotDTOV1) x).getId()));
    }
}

package org.onecx.tenantsvc.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.onecx.tenantsvc.domain.daos.TenantMapDAO;

@ApplicationScoped
public class TenantMapService {

    @Inject
    TenantMapDAO tenantMapDAO;

    public Integer findHighestTenantIdAndAddOne() {

        return tenantMapDAO.findHighestTenantId() + 1;
    }
}

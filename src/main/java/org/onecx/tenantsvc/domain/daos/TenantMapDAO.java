package org.onecx.tenantsvc.domain.daos;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

import org.onecx.tenantsvc.domain.models.TenantMap;
import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class TenantMapDAO extends AbstractDAO<TenantMap> {

    public Optional<String> findTenantIdByOrgId(String orgId) {

        var cb = this.getEntityManager().getCriteriaBuilder();
        var cq = cb.createQuery(TenantMap.class);
        var root = cq.from(TenantMap.class);

        cq.where(cb.equal(root.get("orgId"), orgId));
        var typedQuery = this.em.createQuery(cq);

        try {
            var singleResult = typedQuery.getSingleResult();
            return Optional.of(singleResult.getTenantId());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }
}

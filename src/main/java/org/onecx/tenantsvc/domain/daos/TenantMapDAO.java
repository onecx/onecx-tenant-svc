package org.onecx.tenantsvc.domain.daos;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.onecx.tenantsvc.domain.models.TenantMap;
import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class TenantMapDAO extends AbstractDAO<TenantMap> {

    @Transactional
    public Optional<Integer> findTenantIdByOrgId(String orgId) {

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

    @Transactional
    public Integer findHighestTenantId() {

        var cb = this.getEntityManager().getCriteriaBuilder();
        var cq = cb.createQuery(Integer.class);
        var root = cq.from(TenantMap.class);

        cq.select(cb.max(root.get("tenantId")));
        var typedQuery = this.em.createQuery(cq);
        return typedQuery.getSingleResult();
    }
}

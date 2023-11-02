package org.onecx.tenant.domain.daos;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.onecx.tenant.domain.models.TenantMap;
import org.onecx.tenant.domain.models.TenantMap_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

@ApplicationScoped
public class TenantMapDAO extends AbstractDAO<TenantMap> {

    @Transactional
    public Optional<Integer> findTenantIdByOrgId(String orgId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(TenantMap.class);
            var root = cq.from(TenantMap.class);

            cq.where(cb.equal(root.get(TenantMap_.ORG_ID), orgId));
            var typedQuery = this.em.createQuery(cq);

            var singleResult = typedQuery.getSingleResult();
            return Optional.of(singleResult.getTenantId());

        } catch (NoResultException nre) {
            return Optional.empty();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.FIND_TENANT_ID_BY_ORG_ID, ex);
        }
    }

    public enum ErrorKeys {

        FIND_TENANT_ID_BY_ORG_ID;
    }
}

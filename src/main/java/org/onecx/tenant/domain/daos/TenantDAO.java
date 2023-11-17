package org.onecx.tenant.domain.daos;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.onecx.tenant.domain.criteria.TenantSearchCriteria;
import org.onecx.tenant.domain.models.Tenant;
import org.onecx.tenant.domain.models.Tenant_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.utils.QueryCriteriaUtil;

@ApplicationScoped
public class TenantDAO extends AbstractDAO<Tenant> {

    public PageResult<Tenant> findThemesByCriteria(TenantSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Tenant.class);
            var root = cq.from(Tenant.class);

            if (criteria.getOrgId() != null && !criteria.getOrgId().isBlank()) {
                cq.where(cb.like(root.get(Tenant_.ORG_ID), QueryCriteriaUtil.wildcard(criteria.getOrgId())));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_TENANT_BY_CRITERIA, ex);
        }
    }

    @Transactional
    public Optional<String> findTenantIdByOrgId(String orgId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Tenant.class);
            var root = cq.from(Tenant.class);

            cq.where(cb.equal(root.get(Tenant_.ORG_ID), orgId));
            var typedQuery = this.em.createQuery(cq);

            var singleResult = typedQuery.getSingleResult();
            return Optional.of(singleResult.getTenantId());

        } catch (NoResultException nre) {
            return Optional.empty();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_TENANT_ID_BY_ORG_ID, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_TENANT_BY_CRITERIA,
        ERROR_FIND_TENANT_ID_BY_ORG_ID;
    }
}

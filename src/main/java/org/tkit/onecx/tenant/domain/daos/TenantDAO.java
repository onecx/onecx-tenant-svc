package org.tkit.onecx.tenant.domain.daos;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.onecx.tenant.domain.criteria.TenantSearchCriteria;
import org.tkit.onecx.tenant.domain.models.Tenant;
import org.tkit.onecx.tenant.domain.models.Tenant_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity_;
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
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));
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

    public List<String> filterExistingTenants(Collection<String> tenantIds) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(String.class);
            var root = cq.from(Tenant.class);
            cq.select(root.get(Tenant_.tenantId));
            cq.where(root.get(Tenant_.tenantId).in(tenantIds));
            return this.getEntityManager().createQuery(cq).getResultList();

        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FILTER_EXISTING_TENANTS, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_FILTER_EXISTING_TENANTS,
        ERROR_FIND_TENANT_BY_CRITERIA,
        ERROR_FIND_TENANT_ID_BY_ORG_ID;
    }
}

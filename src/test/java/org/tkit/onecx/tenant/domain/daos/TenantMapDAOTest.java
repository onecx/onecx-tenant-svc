package org.tkit.onecx.tenant.domain.daos;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.tkit.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TenantMapDAOTest extends AbstractTest {
    @Inject
    TenantDAO dao;

    @InjectMock
    EntityManager em;

    @BeforeEach
    void beforeAll() {
        Mockito.when(em.getCriteriaBuilder()).thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void findTenantIdByOrgIdExceptionTest() {
        methodExceptionTests(() -> dao.filterExistingTenants(null),
                TenantDAO.ErrorKeys.ERROR_FILTER_EXISTING_TENANTS);
        methodExceptionTests(() -> dao.findThemesByCriteria(null),
                TenantDAO.ErrorKeys.ERROR_FIND_TENANT_BY_CRITERIA);
        methodExceptionTests(() -> dao.findTenantIdByOrgId(null),
                TenantDAO.ErrorKeys.ERROR_FIND_TENANT_ID_BY_ORG_ID);
    }

    void methodExceptionTests(Executable fn, Enum<?> key) {
        var exc = Assertions.assertThrows(DAOException.class, fn);
        Assertions.assertEquals(key, exc.key);
    }
}

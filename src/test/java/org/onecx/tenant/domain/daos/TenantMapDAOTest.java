package org.onecx.tenant.domain.daos;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TenantMapDAOTest {
    @Inject
    TenantMapDAO dao;

    @InjectMock
    EntityManager em;

    @BeforeEach
    void beforeAll() {
        Mockito.when(em.getCriteriaBuilder()).thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void findTenantIdByOrgIdExceptionTest() {
        var exc = Assertions.assertThrows(DAOException.class, () -> dao.findTenantIdByOrgId(null));
        Assertions.assertEquals(TenantMapDAO.ErrorKeys.FIND_TENANT_ID_BY_ORG_ID,
                exc.key);
    }
}

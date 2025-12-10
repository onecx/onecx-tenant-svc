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

import gen.org.tkit.onecx.image.rs.internal.model.RefTypeDTO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ImageDAOTest extends AbstractTest {
    @Inject
    ImageDAO dao;

    @InjectMock
    EntityManager em;

    @BeforeEach
    void beforeAll() {
        Mockito.when(em.getCriteriaBuilder()).thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.findByRefIdAndRefType(null, null),
                ImageDAO.ErrorKeys.FIND_ENTITY_BY_REF_ID_REF_TYPE_FAILED);
        methodExceptionTests(() -> dao.findByRefIdAndRefType(null, null),
                ImageDAO.ErrorKeys.FIND_ENTITY_BY_REF_ID_REF_TYPE_FAILED);
        methodExceptionTests(() -> dao.deleteQueryByRefIdAndRefType("1", RefTypeDTO.LOGO),
                ImageDAO.ErrorKeys.FAILED_TO_DELETE_BY_REF_ID_REF_TYPE_QUERY);

    }

    void methodExceptionTests(Executable fn, Enum<?> key) {
        var exc = Assertions.assertThrows(DAOException.class, fn);
        Assertions.assertEquals(key, exc.key);
    }
}

package org.tkit.onecx.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.tenant.domain.daos.TenantDAO;
import org.tkit.onecx.tenant.test.AbstractTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@DisplayName("Tenant data import test from example file")
class AfterStartTenantImportTest extends AbstractTest {

    @Inject
    TenantDAO dao;

    @Test
    @DisplayName("Import theme data from file")
    void importDataFromFileTest() {
        var data = dao.findAllAsList();
        assertThat(data).isNotNull().hasSize(2);
    }

}

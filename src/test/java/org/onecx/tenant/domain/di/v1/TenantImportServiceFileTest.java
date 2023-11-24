package org.onecx.tenant.domain.di.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onecx.tenant.domain.daos.TenantDAO;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@DisplayName("Tenant data import test from example file")
@TestProfile(TenantImportServiceFileTest.CustomProfile.class)
class TenantImportServiceFileTest {

    @Inject
    TenantDAO dao;

    @Test
    @DisplayName("Import theme data from file")
    void importDataFromFileTest() {
        var data = dao.findAll().toList();
        assertThat(data).isNotNull().hasSize(2);
    }

    public static class CustomProfile implements QuarkusTestProfile {

        @Override
        public String getConfigProfile() {
            return "test";
        }

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "tkit.dataimport.enabled", "true",
                    "tkit.dataimport.configurations.tenant.enabled", "true",
                    "tkit.dataimport.configurations.tenant.file", "./src/test/resources/import/tenant-import.json",
                    "tkit.dataimport.configurations.tenant.metadata.operation", "CLEAN_INSERT",
                    "tkit.dataimport.configurations.tenant.stop-at-error", "true");
        }
    }
}

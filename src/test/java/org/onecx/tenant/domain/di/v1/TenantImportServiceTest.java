package org.onecx.tenant.domain.di.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onecx.tenant.domain.daos.TenantMapDAO;
import org.onecx.tenant.domain.models.TenantMap;
import org.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.test.WithDBData;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.tenant.di.v1.model.TenantImportDTOV1;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
class TenantImportServiceTest extends AbstractTest {

    @Inject
    TenantImportService service;

    @Inject
    TenantMapDAO dao;

    @Inject
    ObjectMapper mapper;

    @Test
    void importNoneTest() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("operation", "CUSTOM_NOT_SUPPORTED");
        DataImportConfig config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return metadata;
            }
        };
        service.importData(config);

        List<TenantMap> data = dao.findAll().collect(Collectors.toList());
        Assertions.assertNotNull(data);
        Assertions.assertEquals(2, data.size());

        config.getMetadata().put("operation", "NONE");

        data = dao.findAll().collect(Collectors.toList());
        Assertions.assertNotNull(data);
        Assertions.assertEquals(2, data.size());
    }

    @Test
    void importCleanInsertTest() {

        TenantImportDTOV1 data = new TenantImportDTOV1();
        data.put("A", 1);
        data.put("B", 2);
        data.put("C", 3);

        service.importData(new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return Map.of("operation", "CLEAN_INSERT");
            }

            @Override
            public byte[] getData() {
                try {
                    return mapper.writeValueAsBytes(data);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        List<TenantMap> params = dao.findAll().collect(Collectors.toList());
        Assertions.assertNotNull(params);
        Assertions.assertEquals(3, params.size());
    }

    @Test
    void importEmptyDataTest() {
        Assertions.assertDoesNotThrow(() -> {
            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }

                @Override
                public byte[] getData() {
                    return new byte[] {};
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }

                @Override
                public byte[] getData() {
                    try {
                        return mapper.writeValueAsBytes(Map.of());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

        });

        var config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return Map.of("operation", "CLEAN_INSERT");
            }

            @Override
            public byte[] getData() {
                return new byte[] { 0 };
            }
        };
        Assertions.assertThrows(RuntimeException.class, () -> service.importData(config));

    }
}

package org.tkit.onecx.tenant.domain.di.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.tenant.domain.daos.TenantDAO;
import org.tkit.onecx.tenant.domain.models.Tenant;
import org.tkit.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.test.WithDBData;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.tenant.di.v1.model.DataImportDTOV1;
import gen.org.tkit.onecx.tenant.di.v1.model.DataImportTenantDTOV1;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
class TenantImportServiceTest extends AbstractTest {

    @Inject
    TenantImportService service;

    @Inject
    TenantDAO dao;

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

        List<Tenant> data = dao.findAll().toList();
        Assertions.assertNotNull(data);
        Assertions.assertEquals(2, data.size());

        config.getMetadata().put("operation", "NONE");

        data = dao.findAll().toList();
        Assertions.assertNotNull(data);
        Assertions.assertEquals(2, data.size());
    }

    @Test
    void importCleanInsertTest() {

        DataImportDTOV1 data = new DataImportDTOV1();
        var t1 = new DataImportTenantDTOV1();
        t1.setOrgId("1");
        data.putTenantsItem("A", t1);

        var t2 = new DataImportTenantDTOV1();
        t2.setOrgId("2");
        data.putTenantsItem("B", t2);

        var t3 = new DataImportTenantDTOV1();
        t3.setOrgId("3");
        data.putTenantsItem("C", t3);

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

        List<Tenant> params = dao.findAll().toList();
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
                        return mapper.writeValueAsBytes(new DataImportDTOV1());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
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
                        var data = new DataImportDTOV1();
                        data.setTenants(null);
                        return mapper.writeValueAsBytes(data);
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

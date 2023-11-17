package org.onecx.tenant.domain.di.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.onecx.tenant.domain.daos.TenantDAO;
import org.onecx.tenant.domain.models.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.dataimport.DataImport;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.dataimport.DataImportService;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.tenant.di.v1.model.TenantImportDTOV1;

@DataImport("tenant")
public class TenantImportService implements DataImportService {

    private static final Logger log = LoggerFactory.getLogger(TenantImportService.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    TenantDAO dao;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void importData(DataImportConfig config) {
        log.info("Import tenant from configuration {}", config);
        try {
            String operation = config.getMetadata().getOrDefault("operation", "NONE");

            Consumer<TenantImportDTOV1> action = null;
            if ("CLEAN_INSERT".equals(operation)) {
                action = this::cleanInsert;
            }

            if (action == null) {
                log.warn("Not supported operation '{}' for the import configuration key '{}'", operation, config.getKey());
                return;
            }

            if (config.getData() == null || config.getData().length == 0) {
                log.warn("Import configuration key {} does not contains any data to import", config.getKey());
                return;
            }

            TenantImportDTOV1 data = mapper.readValue(config.getData(), TenantImportDTOV1.class);
            if (data.isEmpty()) {
                log.warn("Import configuration key {} does not contains any JSON data to import", config.getKey());
                return;
            }

            // execute the import
            action.accept(data);

        } catch (Exception ex) {
            throw new ErrorImportException(ex);
        }
    }

    private void cleanInsert(TenantImportDTOV1 data) {

        List<Tenant> tmp = new ArrayList<>();
        data.forEach((k, v) -> {
            var t = new Tenant();
            t.setOrgId(k);
            t.setTenantId(v);
            tmp.add(t);
        });

        // delete all mappings
        dao.deleteQueryAll();

        // create new mappings
        dao.create(tmp);
    }

    public static class ErrorImportException extends RuntimeException {
        public ErrorImportException(Exception ex) {
            super(ex.getMessage(), ex);
        }
    }
}

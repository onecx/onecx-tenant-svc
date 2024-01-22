package org.onecx.tenant.test;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.onecx.tenant.rs.external.TenantConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.quarkus.test.Mock;
import io.restassured.config.RestAssuredConfig;
import io.smallrye.config.SmallRyeConfig;

@SuppressWarnings("java:S2187")
public class AbstractTest {

    protected static final String APM_HEADER_TOKEN = ConfigProvider.getConfig()
            .getValue("%test.tkit.rs.context.token.header-param", String.class);

    protected static final String DEFAULT_ID = ConfigProvider.getConfig().getValue("onecx.tenant.default.tenant-id",
            String.class);

    static {
        config = RestAssuredConfig.config().objectMapperConfig(
                objectMapperConfig().jackson2ObjectMapperFactory(
                        (cls, charset) -> {
                            var objectMapper = new ObjectMapper();
                            objectMapper.registerModule(new JavaTimeModule());
                            objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
                            return objectMapper;
                        }));
    }

    public static class ConfigProducer {

        @Inject
        Config config;

        @Produces
        @ApplicationScoped
        @Mock
        TenantConfig config() {
            return config.unwrap(SmallRyeConfig.class).getConfigMapping(TenantConfig.class);
        }
    }

}

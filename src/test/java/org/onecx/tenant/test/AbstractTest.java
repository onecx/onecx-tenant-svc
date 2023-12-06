package org.onecx.tenant.test;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

import org.eclipse.microprofile.config.ConfigProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.restassured.config.RestAssuredConfig;

@SuppressWarnings("java:S2187")
public class AbstractTest {

    protected static final String APM_HEADER_TOKEN = ConfigProvider.getConfig().getValue("onecx.tenant.header.token",
            String.class);

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
}

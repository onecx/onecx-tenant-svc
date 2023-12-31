package org.onecx.tenant.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.onecx.tenant.rs.external.TenantConfig;
import org.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.tenant.v1.model.ProblemDetailResponseDTOV1;
import gen.io.github.onecx.tenant.v1.model.TenantIdDTOV1;
import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.smallrye.config.SmallRyeConfig;

@QuarkusTest
@TestHTTPEndpoint(TenantControllerV1.class)
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
class TenantControllerV1NoDefaultTest extends AbstractTest {

    private static String token;

    private static String tokenWithNotExistingOrgId;

    private static String tokenWithoutOrgId;

    private static final KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @InjectMock
    TenantConfig tenantConfig;

    @Inject
    Config config;

    @BeforeEach
    void beforeEach() {
        var tmp = config.unwrap(SmallRyeConfig.class).getConfigMapping(TenantConfig.class);
        Mockito.when(tenantConfig.defaultTenantEnabled()).thenReturn(false);
        Mockito.when(tenantConfig.headerToken()).thenReturn(tmp.headerToken());
        Mockito.when(tenantConfig.tokenVerified()).thenReturn(tmp.tokenVerified());
        Mockito.when(tenantConfig.tokenPublicKeyLocationSuffix()).thenReturn(tmp.tokenPublicKeyLocationSuffix());
        Mockito.when(tenantConfig.tokenPublicKeyEnabled()).thenReturn(tmp.tokenPublicKeyEnabled());
        Mockito.when(tenantConfig.tokenOrgClaim()).thenReturn(tmp.tokenOrgClaim());
        Mockito.when(tenantConfig.defaultTenantId()).thenReturn(tmp.defaultTenantId());
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

    @BeforeAll
    static void setUp() {
        token = keycloakClient.getAccessToken("user_with_orgId_1234");
        tokenWithNotExistingOrgId = keycloakClient.getAccessToken("user_with_orgId_2222");
        tokenWithoutOrgId = keycloakClient.getAccessToken("user_with_no_orgId");
    }

    @Test
    void getTenantMapsByOrgId_shouldReturnNotFound_whenTenantWithOrgIdDoesNotExist() {

        given().header(tenantConfig.headerToken(), tokenWithNotExistingOrgId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void skipTokenVerified() {

        var dto = given()
                .header(tenantConfig.headerToken(), tokenWithoutOrgId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getErrorCode()).isNotNull()
                .isEqualTo(TenantControllerV1.ErrorKeys.ERROR_NO_ORGANIZATION_ID_IN_TOKEN.name());
    }

    @Test
    void getTenantMapsByOrgId_shouldReturnTenantId() {

        var dto = given()
                .header(tenantConfig.headerToken(), token)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(TenantIdDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getTenantId()).isNotNull().isEqualTo("10");
    }

}

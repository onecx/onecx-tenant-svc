package org.tkit.onecx.tenant.rs.resolver.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.tenant.domain.services.TenantConfig;
import org.tkit.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.tenant.v1.model.TenantIdDTOV1;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.smallrye.config.SmallRyeConfig;

@QuarkusTest
@TestHTTPEndpoint(TenantResolverControllerV1.class)
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "resolver-client", scopes = { "ocx-tn-resolver:read" })
class TenantResolverControllerV1NoDefaultTest extends AbstractTest {

    private static String token;

    private static final KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @InjectMock
    TenantConfig tenantConfig;

    @Inject
    Config config;

    @BeforeEach
    void beforeEach() {
        var tmp = config.unwrap(SmallRyeConfig.class).getConfigMapping(TenantConfig.class);

        TenantConfig.TenantResolverConfig trc = new TenantConfig.TenantResolverConfig() {
            @Override
            public boolean defaultTenantEnabled() {
                return false;
            }

            @Override
            public String defaultTenantId() {
                return "none";
            }
        };

        Mockito.when(tenantConfig.defaultTenantEnabled()).thenReturn(false);
        Mockito.when(tenantConfig.resolver()).thenReturn(trc);
        Mockito.when(tenantConfig.defaultNoClaimTenantEnabled()).thenReturn(tmp.defaultNoClaimTenantEnabled());
        Mockito.when(tenantConfig.defaultNoClaimTenantId()).thenReturn(tmp.defaultNoClaimTenantId());
        Mockito.when(tenantConfig.tokenOrgClaim()).thenReturn(tmp.tokenOrgClaim());
        Mockito.when(tenantConfig.defaultTenantId()).thenReturn(tmp.defaultTenantId());
    }

    @BeforeAll
    static void setUp() {
        token = keycloakClient.getAccessToken("user_with_orgId_1234");
    }

    @Test
    void getTenantByOrgIdDoesNotExistsTest() {

        given()
                .auth().oauth2(getKeycloakClientToken("resolver-client"))
                .header(APM_HEADER_TOKEN, token)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .pathParam("orgId", 1234567)
                .get()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void getTenantByOrgIdExistsTest() {

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("resolver-client"))
                .header(APM_HEADER_TOKEN, token)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .pathParam("orgId", 1234)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(TenantIdDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getTenantId()).isNotNull().isEqualTo("10");
    }

}

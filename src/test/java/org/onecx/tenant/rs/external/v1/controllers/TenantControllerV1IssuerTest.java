package org.onecx.tenant.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.onecx.tenant.rs.external.TenantConfig;
import org.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.tenant.v1.model.TenantIdDTOV1;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.smallrye.config.SmallRyeConfig;

@QuarkusTest
@TestHTTPEndpoint(TenantControllerV1.class)
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
class TenantControllerV1IssuerTest extends AbstractTest {

    @Test
    void useIssuerKeyPublicLocation() {
        KeycloakTestClient keycloakClient = new KeycloakTestClient();
        var token = keycloakClient.getAccessToken("user_with_orgId_1234");
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

    @InjectMock
    TenantConfig tenantConfig;

    @Inject
    Config config;

    @BeforeEach
    void beforeEach() {
        var tmp = config.unwrap(SmallRyeConfig.class).getConfigMapping(TenantConfig.class);
        Mockito.when(tenantConfig.defaultTenantEnabled()).thenReturn(tmp.defaultTenantEnabled());
        Mockito.when(tenantConfig.headerToken()).thenReturn(tmp.headerToken());
        Mockito.when(tenantConfig.tokenVerified()).thenReturn(tmp.tokenVerified());
        Mockito.when(tenantConfig.tokenPublicKeyLocationSuffix()).thenReturn(tmp.tokenPublicKeyLocationSuffix());
        Mockito.when(tenantConfig.tokenPublicKeyEnabled()).thenReturn(true);
        Mockito.when(tenantConfig.tokenOrgClaim()).thenReturn(tmp.tokenOrgClaim());
        Mockito.when(tenantConfig.defaultTenantId()).thenReturn(tmp.defaultTenantId());
    }

}

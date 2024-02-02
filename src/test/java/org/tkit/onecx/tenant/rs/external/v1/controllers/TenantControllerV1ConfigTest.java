package org.tkit.onecx.tenant.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.tenant.rs.external.TenantConfig;
import org.tkit.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.tenant.v1.model.TenantIdDTOV1;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.smallrye.config.SmallRyeConfig;

@QuarkusTest
@TestHTTPEndpoint(TenantControllerV1.class)
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
class TenantControllerV1ConfigTest extends AbstractTest {

    @Test
    void skipTokenVerified() {

        KeycloakTestClient keycloakClient = new KeycloakTestClient();
        var token = keycloakClient.getAccessToken("user_with_orgId_1234");

        var dto = given()
                .header(APM_HEADER_TOKEN, token)
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
        Mockito.when(tenantConfig.tokenOrgClaim()).thenReturn(tmp.tokenOrgClaim());
        Mockito.when(tenantConfig.defaultTenantId()).thenReturn(tmp.defaultTenantId());
    }

}

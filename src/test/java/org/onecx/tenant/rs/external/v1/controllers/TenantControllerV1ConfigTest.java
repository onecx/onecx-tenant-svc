package org.onecx.tenant.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.tenant.v1.model.TenantMapDTOV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@TestHTTPEndpoint(TenantControllerV1.class)
@TestProfile(TenantControllerV1ConfigTest.CustomProfile.class)
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
class TenantControllerV1ConfigTest extends AbstractTest {

    private static final String APM_HEADER_TOKEN = ConfigProvider.getConfig().getValue("onecx.tenant.header.token",
            String.class);

    @Test
    void skipTokenVerified() {
        KeycloakTestClient keycloakClient = new KeycloakTestClient();
        var token = keycloakClient.getAccessToken("user_with_orgId_1234");
        var response = given()
                .header(APM_HEADER_TOKEN, token)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();

        response.then().log().all().statusCode(OK.getStatusCode());
        var tenantDTO = response.as(TenantMapDTOV1.class);
        assertEquals("10", tenantDTO.getTenantId());
    }

    public static class CustomProfile implements QuarkusTestProfile {

        @Override
        public String getConfigProfile() {
            return "test";
        }

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("onecx.tenant.token.verified", "false");
        }
    }
}

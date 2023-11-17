package org.onecx.tenant.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Map;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.tenant.v1.model.ProblemDetailResponseDTOV1;
import gen.io.github.onecx.tenant.v1.model.TenantIdDTOV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@TestHTTPEndpoint(TenantControllerV1.class)
@TestProfile(TenantControllerV1NoDefaultTest.CustomProfile.class)
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
class TenantControllerV1NoDefaultTest extends AbstractTest {

    private static String token;

    private static String tokenWithNotExistingOrgId;

    private static String tokenWithoutOrgId;
    private static final KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private static final String APM_HEADER_TOKEN = ConfigProvider.getConfig().getValue("onecx.tenant.header.token",
            String.class);

    @BeforeAll
    static void setUp() {
        token = keycloakClient.getAccessToken("user_with_orgId_1234");
        tokenWithNotExistingOrgId = keycloakClient.getAccessToken("user_with_orgId_2222");
        tokenWithoutOrgId = keycloakClient.getAccessToken("user_with_no_orgId");
    }

    @Test
    void getTenantMapsByOrgId_shouldReturnNotFound_whenTenantWithOrgIdDoesNotExist() {

        var dto = given().header(APM_HEADER_TOKEN, tokenWithNotExistingOrgId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void skipTokenVerified() {

        var dto = given()
                .header(APM_HEADER_TOKEN, tokenWithoutOrgId)
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

    public static class CustomProfile implements QuarkusTestProfile {

        @Override
        public String getConfigProfile() {
            return "test";
        }

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("onecx.tenant.default.enabled", "false");
        }
    }
}
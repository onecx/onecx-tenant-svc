package org.onecx.tenant.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.tenant.v1.model.TenantIdDTOV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@TestHTTPEndpoint(TenantControllerV1.class)
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
class TenantControllerV1Test extends AbstractTest {

    private static String token;
    private static String tokenWithNotExistingOrgId;
    private static String tokenWithoutOrgId;
    private static final KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    static void setUp() {
        token = keycloakClient.getAccessToken("user_with_orgId_1234");
        tokenWithNotExistingOrgId = keycloakClient.getAccessToken("user_with_orgId_2222");
        tokenWithoutOrgId = keycloakClient.getAccessToken("user_with_no_orgId");
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

    @Test
    void getTenantMapsByOrgId_shouldReturnNotFound_whenTenantWithOrgIdDoesNotExist() {

        var dto = given().header(APM_HEADER_TOKEN, tokenWithNotExistingOrgId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(TenantIdDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getTenantId()).isNotNull().isEqualTo(DEFAULT_ID);
    }

    @Test
    void getTenantMapsByOrgId_shouldReturnBadRequest_whenNoTokenIsPassed() {

        given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given().header(APM_HEADER_TOKEN, "")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        given().header(APM_HEADER_TOKEN, "this_is_not_token")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void getTenantMapsByOrgId_shouldReturnBadRequest_whenTokenUserHasNoOrgId() {

        var dto = given().header(APM_HEADER_TOKEN, tokenWithoutOrgId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(TenantIdDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getTenantId()).isNotNull().isEqualTo(DEFAULT_ID);
    }
}

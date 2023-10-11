package org.onecx.tenantsvc.rs.external.v1;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onecx.tenantsvc.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.tenantsvc.v1.model.ResponseTenantMapDTOV1;
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

        var response = given()
                .auth()
                .oauth2(token)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();

        response.then().statusCode(OK.getStatusCode());
        var tenantDTO = response.as(ResponseTenantMapDTOV1.class).getTenantMap();
        assertEquals(10, tenantDTO.getTenantId());
    }

    @Test
    void getTenantMapsByOrgId_shouldReturnNotFound_whenTenantWithOrgIdDoesNotExist() {

        given()
                .auth()
                .oauth2(tokenWithNotExistingOrgId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void getTenantMapsByOrgId_shouldReturnBadRequest_whenTokenIsNull() {

        given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void getTenantMapsByOrgId_shouldReturnBadRequest_whenTokenUserHasNoOrgId() {

        given()
                .auth()
                .oauth2(tokenWithoutOrgId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }
}
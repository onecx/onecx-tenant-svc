package org.tkit.onecx.tenant.rs.resolver.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.tenant.v1.model.TenantIdDTOV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@TestHTTPEndpoint(TenantResolverControllerV1.class)
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "resolver-client", scopes = { "ocx-tn-resolver:read" })
class TenantResolverControllerV1Test extends AbstractTest {

    private static String token;
    private static final KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    static void setUp() {
        token = keycloakClient.getAccessToken("user_with_orgId_1234");
    }

    @Test
    void getTenantByOrgIdTest() {

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

    @Test
    void getTenantByOrgIdNotFoundTest() {

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("resolver-client"))
                .header(APM_HEADER_TOKEN, token)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .pathParam("orgId", 1234567)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(TenantIdDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getTenantId()).isNotNull().isEqualTo("default");
    }

}

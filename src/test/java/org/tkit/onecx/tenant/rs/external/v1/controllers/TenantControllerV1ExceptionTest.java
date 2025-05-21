package org.tkit.onecx.tenant.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.tenant.domain.daos.TenantDAO;
import org.tkit.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@TestHTTPEndpoint(TenantControllerV1.class)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-tn:read" })
class TenantControllerV1ExceptionTest extends AbstractTest {

    @InjectMock
    TenantDAO dao;

    private static final KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findTenantIdByOrgId("1234"))
                .thenThrow(new RuntimeException("Test technical error exception"))
                .thenThrow(new DAOException(TenantDAO.ErrorKeys.ERROR_FIND_TENANT_ID_BY_ORG_ID, new RuntimeException("Test")));
    }

    @Test
    void exceptionTest() {
        String token = keycloakClient.getAccessToken("user_with_orgId_1234");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_TOKEN, token)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then().statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_TOKEN, token)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then().statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
    }
}

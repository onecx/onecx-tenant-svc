package org.onecx.tenant.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.onecx.tenant.domain.daos.TenantMapDAO.ErrorKeys.FIND_TENANT_ID_BY_ORG_ID;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.onecx.tenant.domain.daos.TenantMapDAO;
import org.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.io.github.onecx.tenant.v1.model.RestExceptionDTOV1;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@TestHTTPEndpoint(TenantControllerV1.class)
class TenantControllerV1ExceptionTest extends AbstractTest {

    @InjectMock
    TenantMapDAO dao;

    private static final KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private static final String APM_HEADER_TOKEN = ConfigProvider.getConfig().getValue("onecx.tenant-svc.header.token",
            String.class);

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findTenantIdByOrgId("1234"))
                .thenThrow(new RuntimeException("Test technical error exception"))
                .thenThrow(new DAOException(FIND_TENANT_ID_BY_ORG_ID, new RuntimeException("Test")));
    }

    @Test
    void exceptionTest() {
        String token = keycloakClient.getAccessToken("user_with_orgId_1234");

        var response = given()
                .header(APM_HEADER_TOKEN, token)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();

        response.then().statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
        var dto = response.as(RestExceptionDTOV1.class);
        assertThat(dto.getErrorCode()).isEqualTo("UNDEFINED_ERROR_CODE");

        response = given()
                .header(APM_HEADER_TOKEN, token)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
        response.then().statusCode(BAD_REQUEST.getStatusCode());
        dto = response.as(RestExceptionDTOV1.class);
        assertThat(dto.getErrorCode()).isEqualTo(FIND_TENANT_ID_BY_ORG_ID.name());
    }
}

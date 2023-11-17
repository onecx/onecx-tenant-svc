package org.onecx.tenant.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.onecx.tenant.domain.daos.TenantDAO;
import org.onecx.tenant.test.AbstractTest;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(TenantControllerInternal.class)
class TenantControllerInternalExceptionTest extends AbstractTest {

    @InjectMock
    TenantDAO dao;

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findById(any())).thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void exceptionTest() {
        given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get("1234")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
    }
}

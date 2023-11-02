package org.onecx.tenant.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.onecx.tenant.domain.daos.TenantMapDAO;
import org.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.jpa.daos.Page;

import gen.io.github.onecx.tenant.rs.internal.model.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(TenantControllerInternal.class)
class TenantControllerInternalExceptionTest extends AbstractTest {

    @InjectMock
    TenantMapDAO dao;

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.createPageQuery(Page.of(0, 10)))
                .thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void exceptionTest() {
        var response = given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();

        response.then().statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

        var dto = response.as(RestExceptionDTO.class);

        assertThat(dto.getErrorCode()).isEqualTo("UNDEFINED_ERROR_CODE");
    }
}

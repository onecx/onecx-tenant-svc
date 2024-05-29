package org.tkit.onecx.tenant.rs.exim.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.tenant.rs.exim.v1.mappers.EximExceptionMapperV1;
import org.tkit.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.tenant.exim.v1.model.EximProblemDetailResponseDTOV1;
import gen.org.tkit.onecx.tenant.exim.v1.model.EximTenantDTOV1;
import gen.org.tkit.onecx.tenant.exim.v1.model.TenantsSnapshotDTOV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(TenantEximV1Controller.class)
@WithDBData(value = { "testdata/tenant-exim-v1.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
class TenantEximV1ControllerTest extends AbstractTest {

    @Test
    void operatorImportNullProductTest() {
        var request = new TenantsSnapshotDTOV1()
                .putTenantsItem("test1", null);

        given()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(OK.getStatusCode());

        request.setTenants(null);
        given()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(OK.getStatusCode());

        request.setTenants(Map.of());
        given()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void operatorImportNewTenantTest() {
        var request = new TenantsSnapshotDTOV1()
                .putTenantsItem("new_tenant_id", new EximTenantDTOV1().description("description").orgId("o1"));

        given()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void operatorImportTest() {

        var request = new TenantsSnapshotDTOV1()
                .putTenantsItem("10", new EximTenantDTOV1().description("description").orgId("o1"))
                .putTenantsItem("new_tenant_id", new EximTenantDTOV1().description("description").orgId("o2"));

        given()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void operatorImportEmptyBodyTest() {

        var dto = given()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(EximProblemDetailResponseDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getErrorCode()).isEqualTo(EximExceptionMapperV1.ErrorCode.CONSTRAINT_VIOLATIONS.name());
        assertThat(dto.getDetail()).isEqualTo(
                "operatorImportTenants.tenantsSnapshotDTOV1: must not be null");
    }
}

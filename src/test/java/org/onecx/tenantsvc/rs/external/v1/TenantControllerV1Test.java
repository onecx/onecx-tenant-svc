package org.onecx.tenantsvc.rs.external.v1;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import org.junit.jupiter.api.Test;
import org.onecx.tenantsvc.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(TenantControllerV1.class)
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
class TenantControllerV1Test extends AbstractTest {

    // TODO enable tests when security schemas are discussed (Ticket: P002271-5604)
    /**
     * @Test
     *       void getTenantMapsByOrgId_shouldReturnTenantId() {
     *
     *       var orgId = "1234";
     *
     *       var response = given()
     *       .contentType(APPLICATION_JSON)
     *       .accept(APPLICATION_JSON)
     *       .get();
     *
     *       response.then().statusCode(OK.getStatusCode());
     *       var tenantDTO = response.as(ResponseTenantMapDTOV1.class).getTenantMap();
     *       assertEquals(10, tenantDTO.getTenantId());
     *       }
     *
     * @Test
     *       void getTenantMapsByOrgId_shouldReturnNotFound_whenTenantWithOrgIdDoesNotExist() {
     *
     *       var orgId = "does-not-exist";
     *
     *       given()
     *       .contentType(APPLICATION_JSON)
     *       .accept(APPLICATION_JSON)
     *       .get()
     *       .then()
     *       .statusCode(NOT_FOUND.getStatusCode());
     *       }
     **/
    @Test
    void getTenantMapsByOrgId_shouldReturnBadRequest_whenTokenIsNull() {

        given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }
}

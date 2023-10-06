package org.onecx.tenantsvc.rs.external.v1;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.onecx.tenantsvc.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.tenantsvc.v1.model.TenantResponseDTOV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithDBData(value = {"testdata/tenant-testdata.xml"}, deleteBeforeInsert = true, rinseAndRepeat = true)
class TenantControllerV1Test extends AbstractTest {

    @TestHTTPEndpoint(TenantControllerV1.class)
    @TestHTTPResource
    String getUserOrderUrl;

    @Test
    void getTenantsByOrgId_shouldReturnTenantId() {

        var orgId = "1234";

        var response = given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(getUserOrderUrl, orgId);

        response.then().statusCode(OK.getStatusCode());
        var tenantDTO = response.as(TenantResponseDTOV1.class);
        assertEquals("10", tenantDTO.getTenantId());
    }

    @Test
    void getTenantsByOrgId_shouldReturnNotFound_whenTenantWithOrgIdDoesNotExist() {

        var orgId = "does-not-exist";

        var response = given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(getUserOrderUrl, orgId);

        response.then().statusCode(NOT_FOUND.getStatusCode());
    }
}

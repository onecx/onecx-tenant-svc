package org.onecx.tenant.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.tenant.rs.internal.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
@TestHTTPEndpoint(TenantControllerInternal.class)
class TenantControllerInternalTest extends AbstractTest {

    @Test
    void getAllTenantMapsPageable() {

        var pageNumber = 0;
        var pageSize = 5;
        var response = given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .queryParam("pageNumber", pageNumber)
                .queryParam("pageSize", pageSize)
                .get();

        response.then().statusCode(OK.getStatusCode());
        var tenantMapDTOS = response.as(ResponseTenantMapsDTO.class);
        var tenantMaps = tenantMapDTOS.getTenantMaps();
        assertThat(tenantMaps).hasSize(2);
    }

    @Test
    void updateTenantMapById() {

        var orgId = "5678";
        var requestTenantMapDTO = new RequestTenantMapDTO();
        var inputTenantMapDTO = new InputTenantMapDTO();
        inputTenantMapDTO.setOrgId(orgId);
        requestTenantMapDTO.setInputTenantMap(inputTenantMapDTO);

        var response = given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(requestTenantMapDTO)
                .put("1");

        response.then().statusCode(OK.getStatusCode());
        var tenantMapDTO = response.as(ResponseTenantMapDTO.class).getTenantMap();
        assertThat(tenantMapDTO.getTenantId()).isEqualTo("10");
        assertThat(tenantMapDTO.getOrgId()).isEqualTo(orgId);
    }

    @Test
    void updateTenantMapById_shouldReturnNotFound_whenEntityDoesNotExist() {

        var orgId = "5678";
        var requestTenantMapDTO = new RequestTenantMapDTO();
        var inputTenantMapDTO = new InputTenantMapDTO();
        inputTenantMapDTO.setOrgId(orgId);
        requestTenantMapDTO.setInputTenantMap(inputTenantMapDTO);

        given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(requestTenantMapDTO)
                .put("3")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void updateTenantMapById_shouldReturnBadRequest_whenOrgIdUniqueConstraintIsViolated() {

        var orgId = "1111";
        var requestTenantMapDTO = new RequestTenantMapDTO();
        var inputTenantMapDTO = new InputTenantMapDTO();
        inputTenantMapDTO.setOrgId(orgId);
        requestTenantMapDTO.setInputTenantMap(inputTenantMapDTO);

        given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(requestTenantMapDTO)
                .put("1")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void createTenantMap() {

        var requestTenantMapDTO = new CreateRequestTenantMapDTO();
        var inputTenantMapDTO = new CreateInputTenantMapDTO();
        inputTenantMapDTO.setOrgId("5678");
        inputTenantMapDTO.setTenantId("12");
        requestTenantMapDTO.setInputTenantMap(inputTenantMapDTO);

        var uri = given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(requestTenantMapDTO)
                .post()
                .then().statusCode(CREATED.getStatusCode())
                .extract().header(HttpHeaders.LOCATION);

        var dto = given()
                .contentType(APPLICATION_JSON)
                .get(uri)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(TenantMapDTO.class);

        assertThat(dto.getTenantId()).isEqualTo(inputTenantMapDTO.getTenantId());
        assertThat(dto.getOrgId()).isEqualTo(inputTenantMapDTO.getOrgId());
    }

    @Test
    void createTenantMap_EmptyBody() {

        given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post()
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void createTenantMap_shouldReturnBadRequest_whenOrgIdUniqueConstraintIsViolated() {

        var orgId = "1234";
        var requestTenantMapDTO = new CreateRequestTenantMapDTO();
        var inputTenantMapDTO = new CreateInputTenantMapDTO();
        inputTenantMapDTO.setOrgId(orgId);
        requestTenantMapDTO.setInputTenantMap(inputTenantMapDTO);

        given()
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(requestTenantMapDTO)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }
}

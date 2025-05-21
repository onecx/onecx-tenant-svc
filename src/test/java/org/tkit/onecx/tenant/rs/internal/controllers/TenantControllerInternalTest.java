package org.tkit.onecx.tenant.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.tenant.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.tenant.rs.internal.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithDBData(value = { "testdata/tenant-testdata.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
@TestHTTPEndpoint(TenantControllerInternal.class)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-tn:read", "ocx-tn:write", "ocx-tn:all" })
class TenantControllerInternalTest extends AbstractTest {

    @Test
    void getAllTenantMapsPageable() {

        var criteria = new TenantSearchCriteriaDTO();

        criteria.setPageNumber(0);
        criteria.setPageSize(5);

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(criteria)
                .post("search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(TenantPageResultDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getStream()).isNotNull().hasSize(2);

        criteria.setOrgId(" ");

        dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(criteria)
                .post("search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(TenantPageResultDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getStream()).isNotNull().hasSize(2);

        criteria.setOrgId("1234");

        dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(criteria)
                .post("search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(TenantPageResultDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getStream()).isNotNull().hasSize(1);
    }

    @Test
    void updateTenantMapById() {

        var updateRequest = new UpdateTenantRequestDTO();
        updateRequest.setOrgId("5678");

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(updateRequest)
                .put("1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(TenantDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getOrgId()).isNotNull().isEqualTo(updateRequest.getOrgId());
        assertThat(dto.getTenantId()).isNotNull().isEqualTo("10");
    }

    @Test
    void updateTenantMapById_shouldReturnNotFound_whenEntityDoesNotExist() {

        var request = new UpdateTenantRequestDTO();
        request.setOrgId("5678");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(request)
                .put("3")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void updateTenantMapById_shouldReturnBadRequest_whenOrgIdUniqueConstraintIsViolated() {

        var request = new UpdateTenantRequestDTO();
        request.setOrgId("1111");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(request)
                .put("1")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void getTenantTest() {

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get("1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(TenantDTO.class);

        assertThat(dto.getTenantId()).isEqualTo("10");
        assertThat(dto.getOrgId()).isEqualTo("1234");
    }

    @Test
    void getTenantWrongIdTest() {

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get("does-not-exists")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void createTenantTest() {

        var request = new CreateTenantRequestDTO();
        request.setOrgId("12345");
        request.setTenantId("12");

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().as(TenantDTO.class);

        assertThat(dto.getTenantId()).isEqualTo(request.getTenantId());
        assertThat(dto.getOrgId()).isEqualTo(request.getOrgId());
    }

    @Test
    void createTenantConstraintTest() {

        var request = new CreateTenantRequestDTO();
        request.setOrgId("1234");
        request.setTenantId("12");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void createTenantMap_EmptyBody() {

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post()
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void createTenantMap_shouldReturnBadRequest_whenOrgIdUniqueConstraintIsViolated() {

        var request = new CreateTenantRequestDTO();
        request.setOrgId("1234");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }
}

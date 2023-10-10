package org.onecx.tenantsvc.rs.internal.services;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.io.github.onecx.tenantsvc.rs.internal.model.ErrorResponseDTO;

@ApplicationScoped
public class ErrorResponseService {

    public ErrorResponseDTO createErrorResponse(DAOException pe) {

        var key = pe.key.name();
        var parameters = pe.parameters.stream().map(Object::toString).toList();
        var details = pe.namedParameters.entrySet()
                .stream()
                .map(e -> e.getKey() + ": " + e.getValue().toString())
                .toList();

        var errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setKey(key);
        errorResponseDTO.setParameters(parameters);
        errorResponseDTO.setDetails(details);
        return errorResponseDTO;
    }
}

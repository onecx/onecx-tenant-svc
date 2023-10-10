package org.onecx.tenantsvc.rs.internal.models;

import java.util.List;

import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    String key;
    List<String> parameters;
    List<String> details;
}

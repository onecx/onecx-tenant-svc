package org.onecx.tenantsvc.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@Entity
@Table(name = "tenant_map")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TenantMap extends TraceableEntity {

    @Column(name = "org_id", unique = true)
    private String orgId;

    @Column(name = "tenant_id", unique = true)
    private Integer tenantId;
}
